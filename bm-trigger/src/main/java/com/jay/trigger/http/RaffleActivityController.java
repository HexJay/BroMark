package com.jay.trigger.http;


import com.jay.api.IRaffleActivityService;
import com.jay.api.dto.ActivityDrawRequestDTO;
import com.jay.api.dto.ActivityDrawResponseDTO;
import com.jay.api.response.Response;
import com.jay.domain.activity.model.entity.UserRaffleOrderEntity;
import com.jay.domain.activity.service.IRaffleActivityPartakeService;
import com.jay.domain.activity.service.armory.IActivityArmory;
import com.jay.domain.award.model.entity.UserAwardRecordEntity;
import com.jay.domain.award.model.vo.AwardStateVO;
import com.jay.domain.award.service.IAwardService;
import com.jay.domain.strategy.model.entity.RaffleAwardEntity;
import com.jay.domain.strategy.model.entity.RaffleFactorEntity;
import com.jay.domain.strategy.service.IRaffleStrategy;
import com.jay.domain.strategy.service.armory.IStrategyArmory;
import com.jay.types.enums.ResponseCode;
import com.jay.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author Jay
 * @date 2025/7/26 16:04
 * @description 抽奖活动服务
 */
@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/activity")
public class RaffleActivityController implements IRaffleActivityService {

    @Resource
    private IActivityArmory activityArmory;
    @Resource
    private IStrategyArmory strategyArmory;

    @Resource
    private IRaffleActivityPartakeService raffleActivityPartakeService;
    @Resource
    private IRaffleStrategy raffleStrategy;
    @Resource
    private IAwardService awardService;

    /**
     * 活动装配 - 数据预热 | 把活动配置的对应的 sku 一起装配
     *
     * @param activityId 活动ID
     * @return 装配结果
     * <p>
     * 接口：<a href="http://localhost:8091/api/v1/raffle/activity/armory">/api/v1/raffle/activity/armory</a>
     * <p>
     * 入参：{"activityId":100001,"userId":"Jay"}
     * <p>
     * curl --request GET \
     * --url 'http://localhost:8091/api/v1/raffle/activity/armory?activityId=100301'
     */
    @Override
    @RequestMapping(value = "armory", method = RequestMethod.GET)
    public Response<Boolean> armory(@RequestParam Long activityId) {
        try {
            activityArmory.assembleActivitySkuByActivity(activityId);
            strategyArmory.assembleLotteryStrategyByActivityId(activityId);
            return Response.ok(true);
        } catch (Exception e) {
            log.error("活动装配，数据预热，失败 activityId:{}", activityId, e);
            return Response.fail(ResponseCode.UN_ERROR, null);
        }
    }
    /**
     * 抽奖接口
     *
     * @param request 请求对象
     * @return 抽奖结果
     * <p>
     * 接口：<a href="http://localhost:8091/api/v1/raffle/activity/draw">/api/v1/raffle/activity/draw</a>
     * 入参：{"activityId":100001,"userId":"Jay"}
     * <p>
     * curl --request POST \ <p>
     *   --url http://localhost:8091/api/v1/raffle/activity/draw \ <p>
     *   --header 'content-type: application/json' \ <p>
     *   --data '{ <p>
     *     "userId":"xiaofuge", <p>
     *     "activityId": 100301 <p>
     * }'
     */
    @Override
    @RequestMapping(value = "draw", method = RequestMethod.POST)
    public Response<ActivityDrawResponseDTO> draw(@RequestBody ActivityDrawRequestDTO request) {
        try {
            log.info("活动抽奖-开始 userId:{} activityId:{}", request.getUserId(), request.getActivityId());
            // 1.参数校验
            if (StringUtils.isBlank(request.getUserId()) || request.getActivityId() == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            // 2.参与活动 - 创建参与记录订单
            UserRaffleOrderEntity order = raffleActivityPartakeService.createOrder(request.getUserId(), request.getActivityId());
            log.info("活动抽奖-创建订单 userId:{} activityId:{} orderId:{}", request.getUserId(), request.getActivityId(), order.getOrderId());
            // 3.抽奖策略 - 执行抽奖
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                    .userId(order.getUserId())
                    .strategyId(order.getStrategyId())
                    .build());
            log.info("活动抽奖-执行抽奖 userId:{} awardId:{}", order.getUserId(), raffleAwardEntity.getAwardId());
            // 4.存放结果 - 写入中奖记录
            UserAwardRecordEntity userAwardRecordEntity = UserAwardRecordEntity.builder()
                    .userId(order.getUserId())
                    .activityId(order.getActivityId())
                    .strategyId(order.getStrategyId())
                    .orderId(order.getOrderId())
                    .awardId(raffleAwardEntity.getAwardId())
                    .awardTitle(raffleAwardEntity.getAwardTitle())
                    .awardTime(new Date())
                    .awardState(AwardStateVO.create)
                    .build();
            awardService.saveUserAwardRecord(userAwardRecordEntity);
            // 5.返回结果
            return Response.ok(ActivityDrawResponseDTO.builder()
                    .awardId(raffleAwardEntity.getAwardId())
                    .awardTitle(raffleAwardEntity.getAwardTitle())
                    .awardIndex(raffleAwardEntity.getSort())
                    .build());
        } catch (AppException e) {
            log.error("活动抽奖失败 userId:{} activityId:{}", request.getUserId(), request.getActivityId(), e);
            return Response.fail(e.getCode(), e.getInfo());
        } catch (Exception e) {
            log.error("活动抽奖失败 userId:{} activityId:{}", request.getUserId(), request.getActivityId(), e);
            return Response.fail(ResponseCode.UN_ERROR, null);
        }
    }
}
