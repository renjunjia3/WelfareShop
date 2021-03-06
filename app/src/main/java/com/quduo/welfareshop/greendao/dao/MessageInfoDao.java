package com.quduo.welfareshop.greendao.dao;

import com.quduo.welfareshop.greendao.GreenDaoManager;
import com.quduo.welfareshop.greendao.gen.ChatMessageInfoDao;
import com.quduo.welfareshop.ui.friend.entity.ChatMessageInfo;

import org.greenrobot.greendao.query.Query;

import java.util.List;

public class MessageInfoDao {

    private final GreenDaoManager daoManager;
    private static MessageInfoDao mUserDao;

    public MessageInfoDao() {
        daoManager = GreenDaoManager.getInstance();
    }

    public static MessageInfoDao getInstance() {
        if (mUserDao == null) {
            mUserDao = new MessageInfoDao();
        }
        return mUserDao;
    }

    /**
     * 插入数据 若未建表则先建表
     *
     * @param messageInfo
     * @return
     */
    public boolean insertUserData(ChatMessageInfo messageInfo) {
        boolean flag = false;
        flag = getChatMessageInfoDao().insert(messageInfo) != -1;
        return flag;
    }

    /**
     * 插入或替换数据
     *
     * @param messageInfo
     * @return
     */
    public boolean insertOrReplaceData(ChatMessageInfo messageInfo) {
        boolean flag = false;
        try {
            flag = getChatMessageInfoDao().insertOrReplace(messageInfo) == -1 ? false : true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 插入多条数据  子线程完成
     *
     * @param list
     * @return
     */
    public boolean insertOrReplaceMultiData(final List<ChatMessageInfo> list) {
        boolean flag = false;
        try {
            getChatMessageInfoDao().getSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (ChatMessageInfo userInfo : list) {
                        daoManager.getDaoSession().insertOrReplace(userInfo);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 更新数据
     *
     * @param messageInfo
     * @return
     */
    public boolean updateUserData(ChatMessageInfo messageInfo) {
        boolean flag = false;
        try {
            getChatMessageInfoDao().update(messageInfo);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 根据id删除数据
     *
     * @param messageInfo
     * @return
     */
    public boolean deleteUserData(ChatMessageInfo messageInfo) {
        boolean flag = false;
        try {
            getChatMessageInfoDao().delete(messageInfo);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除所有数据
     *
     * @return
     */
    public boolean deleteAllData() {
        boolean flag = false;
        try {
            getChatMessageInfoDao().deleteAll();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 根据主键查询
     *
     * @param key
     * @return
     */
    public ChatMessageInfo queryMessageDataById(long key) {
        return getChatMessageInfoDao().load(key);
    }

    /**
     * 查询所有数据
     *
     * @return
     */
    public List<ChatMessageInfo> queryAllMessageData() {
        return getChatMessageInfoDao().loadAll();
    }

    /**
     * 根据id查询 以时间降序排列
     *
     * @param userId
     * @param otherUserId
     * @return
     */
    public List<ChatMessageInfo> queryUserByUserId(String userId, String otherUserId) {
        Query<ChatMessageInfo> build = null;
        try {
            build = getChatMessageInfoDao().queryBuilder()
                    .where(ChatMessageInfoDao.Properties.OtherUserId.eq(otherUserId))
                    .where(ChatMessageInfoDao.Properties.UserId.eq(userId))
                    .orderAsc(ChatMessageInfoDao.Properties.Time)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.list();
    }

    /**
     * 根据参数查询
     *
     * @param where
     * @param param
     * @return
     */
    public List<ChatMessageInfo> queryMessageInfoByParams(String where, String... param) {
        return getChatMessageInfoDao().queryRaw(where, param);
    }
    /**
    *Author:scene
    *Time:2018/2/23 14:14
    *Description:获取用户的所有的会话列表
    */
    public List<ChatMessageInfo> querySession(String userId) {
        //select * from CHAT_MESSAGE_INFO where USER_ID = '10001' group by OTHER_USER_ID  order by time desc
        return getChatMessageInfoDao().queryRaw("WHERE USER_ID = " + userId + " group by OTHER_USER_ID order by time desc ");
    }

    public void deleteSession(String userId, String otherId) {
        String sql = "DELETE FROM " + ChatMessageInfoDao.TABLENAME + " WHERE ( " + ChatMessageInfoDao.TABLENAME + ".OTHER_USER_ID = " + otherId + " AND " + ChatMessageInfoDao.TABLENAME + ".USER_ID = " + userId + " )";
        getChatMessageInfoDao().getDatabase().execSQL(sql);
    }

    public ChatMessageInfoDao getChatMessageInfoDao() {
        return daoManager.getDaoSession().getChatMessageInfoDao();
    }
}