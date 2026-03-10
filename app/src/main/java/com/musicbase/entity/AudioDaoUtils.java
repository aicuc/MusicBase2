package com.musicbase.entity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class AudioDaoUtils {
    private AudioItemDao audioItemDao;
    private Context context;


    public AudioDaoUtils(Context context) {
        this.context = context;
        SQLiteDatabase writableDatabase = DBManager.getInstance(context).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(writableDatabase);
        DaoSession daoSession = daoMaster.newSession();
        audioItemDao = daoSession.getAudioItemDao();
    }


    public void insertBase(AudioItem audioItem) {
        if (audioItem == null) {
            return;
        }
        audioItemDao.insert(audioItem);
    }

    /**
     * 更新账户
     *
     * @param audioItem 用户账户
     */
    public void updateBase(AudioItem audioItem) {
        if (audioItem == null) {
            return;
        }
        audioItemDao.update(queryBase(audioItem.getField()));
    }

    /**
     * 更新或上传账户
     *
     * @param audioItem 用户账户
     */
    public void updateOrInsertBase(AudioItem audioItem) {
        if (audioItem == null) {
            return;
        }
        if (queryBaseNum(audioItem.getField()) != 0)
            deleteAudioItem(audioItem.getField());

        insertBase(audioItem);
    }


    /**
     * 查询所有用户列表
     */
    public List<AudioItem> queryAudioItems() {
        QueryBuilder<AudioItem> qb = audioItemDao.queryBuilder();
        return qb.list();
    }

    /**
     * 查询所有用户数量
     */
    public int queryBaseNum(String fieldId) {
        QueryBuilder<AudioItem> qb = audioItemDao.queryBuilder();
        qb.where(AudioItemDao.Properties.Field.eq(fieldId));
        return qb.list().size();
    }

    /**
     * 查询单个
     */
    public AudioItem queryBase(String fieldId) {
        QueryBuilder<AudioItem> qb = audioItemDao.queryBuilder();
        qb.where(AudioItemDao.Properties.Field.eq(fieldId));
        return qb.list().get(0);
    }

    /**
     * 删除全部
     */
    public void deleteAudioItemAll() {
        audioItemDao.deleteAll();

    }

    /**
     * 查询用户个数
     */
    public void deleteAudioItem(String fieldId) {
        QueryBuilder<AudioItem> qb = audioItemDao.queryBuilder();
        qb.where(AudioItemDao.Properties.Field.eq(fieldId)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

}