package com.example.mybluetooth.OrmSqlLite;

/**
 * Created by Administrator on 2018/3/22 0022.
 */


import android.content.Context;

import com.example.mybluetooth.App;
import com.example.mybluetooth.OrmSqlLite.Bean.BluetoothDeviceBean;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2016/10/14.
 * 封装各种操作数据库的方法
 */
public class DBManager {
    private static DBManager dbManager;

    static {
        try {
            dbManager = new DBManager(App.getContext());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private Dao dao;

    public DBManager(Context context) throws SQLException {

        OrmHelper ormHelper = new OrmHelper(context);
        //        SQLiteDatabase readableDatabase = ormHelper.getReadableDatabase(); //可以获取一个原生的数据库
        //Dao相当于原生的SQLiteDatabase，可以操作数据库,一个Dao只能操作一张表
        dao = ormHelper.getDao(BluetoothDeviceBean.class);
    }

    /**
     * 插入数据
     *
     * @param BluetoothDeviceBean
     * @thro
     */
    public void insertBluetoothDeviceBean(BluetoothDeviceBean BluetoothDeviceBean) throws SQLException {
        //在数据库中创建一条记录，作用与SQLiteDatabase.insert一样
        dao.create(BluetoothDeviceBean);
    }

    /**
     * 批量插入
     * 不能使用循环一个一个的插入，因为这样会一直打开数据库、插入数据、
     * 关闭数据库
     *
     * @param BluetoothDeviceBean
     * @throws SQLException
     */
    public void batchInsert(List<BluetoothDeviceBean> BluetoothDeviceBean) throws SQLException {
        dao.create(BluetoothDeviceBean);
    }

    /**
     * 查询数据
     *
     * @return
     * @throws SQLException
     */
    public List<BluetoothDeviceBean> getBluetoothDeviceBeanList() throws SQLException {
        List<BluetoothDeviceBean> list = dao.queryForAll();
        return list;
    }


    /**
     * 查询某个数值的数据
     *
     * @return
     * @throws SQLException
     */
    public List<BluetoothDeviceBean> querySelect(String key, String value) throws SQLException {
        //Eq是equals的缩写
        //方法1
        //List<BluetoothDeviceBean> list = dao.queryForEq("name", "张飞");

        //方法2
        QueryBuilder queryBuilder = dao.queryBuilder();
//        queryBuilder.offset(); //偏移量
//        queryBuilder.limit(8l); //最多几行  offset + limit 做分页
//        queryBuilder.orderBy("age",true);
        queryBuilder.where().eq(key, value); //多条件查询
        List<BluetoothDeviceBean> query = queryBuilder.query();//此方法相当于build，提交设置
        return query;
    }


    /**
     * 删除数据
     *
     * @param BluetoothDeviceBean
     * @throws SQLException
     */
    public void deleteBluetoothDeviceBean(BluetoothDeviceBean BluetoothDeviceBean) throws SQLException {
        //只看id
        dao.delete(BluetoothDeviceBean);
    }

    /**
     * 删除指定数据
     *
     * @throws SQLException
     */
    public void deleteBluetoothDeviceBeanID(String id) throws SQLException {
        DeleteBuilder deleteBuilder = dao.deleteBuilder();
        deleteBuilder.where().eq("id", id);
        deleteBuilder.delete();
    }

    /**
     * 修改数据
     *
     * @param BluetoothDeviceBean
     * @throws SQLException
     */
    public void updateName(BluetoothDeviceBean BluetoothDeviceBean, String name) throws SQLException {
        BluetoothDeviceBean.setName(name);
        dao.update(BluetoothDeviceBean);
    }

    /**
     * 修改指定数据
     *
     * @throws SQLException
     */
    public void update() throws SQLException {
        UpdateBuilder updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq("name", "关羽");
        updateBuilder.updateColumnValue("sex", "女");
        updateBuilder.update();
    }
}
