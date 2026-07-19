package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;

import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.service.AiAssistantService;
import org.springframework.stereotype.Service;

@Service
public class AiAssistantServiceImpl implements AiAssistantService {

    @Override
    public Result<ChatReply> chat(String message) {
        String reply;
        if (message == null || message.trim().isEmpty()) {
            reply = "你好！我是校园二手交易平台的AI助手，可以帮你解答使用问题。你可以问我：如何发布商品、如何购买、订单状态等。";
        } else {
            String msg = message.trim();
            if ((msg.contains("发布") || msg.contains("商品")) && msg.contains("怎么")) {
                reply = "在首页点击\"发布商品\"按钮，填写商品信息并上传图片即可发布。发布后需要管理员审核。";
            } else if (msg.contains("买") || msg.contains("购买") || msg.contains("下单")) {
                reply = "在商品列表找到心仪商品，点击进入详情页，选择\"立即购买\"或\"加入购物车\"。";
            } else if (msg.contains("卖家") || msg.contains("联系") || msg.contains("沟通")) {
                reply = "在商品详情页可以给卖家发送消息，或在聊天页面与卖家沟通。";
            } else if (msg.contains("订单") || msg.contains("状态")) {
                reply = "订单状态包括：待付款、待发货、待收货、已完成、已取消。";
            } else if (msg.contains("二手") || msg.contains("交易")) {
                reply = "校园二手交易平台支持商品发布、浏览、购买、聊天、订单管理等功能。你可以问我任何使用问题！";
            } else {
                reply = "你好！我是校园二手交易平台的AI助手，可以帮你解答使用问题。你可以问我：如何发布商品、如何购买、订单状态等。";
            }
        }
        return Result.success(new ChatReply(reply));
    }
}
