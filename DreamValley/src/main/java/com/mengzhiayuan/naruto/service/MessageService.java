package com.mengzhiayuan.naruto.service;

import com.mengzhiayuan.naruto.entity.Message;

import java.util.List;

/**
 * @Auther: 梦致A远
 * @Date: 2021/8/30 11:46
 * @Description:
 */

public interface MessageService {

    List<Message> findConversations(int userId, int offset, int limit);

    int findConversationCount(int userId);

    List<Message> findLetters(String conversationId, int offset, int limit);

    int findLetterCount(String conversationId);

    int findLetterUnreadCount(int userId, String conversationId);

    int addMessage(Message message);

    //将私信的未读状态变成已读
    int readMessage(List<Integer> ids);

    //系统通知

    Message findLatestNotice(int userId, String topic);

    int findNoticeCount(int userId, String topic);

    int findNoticeUnreadCount(int userId, String topic);

    List<Message> findNotices(int userId, String topic, int offset, int limit);
}
