package com.example.mybluetooth.OrmSqlLite.Bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
/*保存下拉框历史记录*/
@DatabaseTable(tableName = "MessageModel")
public class MessageModel implements Serializable {
    @DatabaseField(generatedId = true) //generatedId = true 表示自增长的主键
    private int id;
    @DatabaseField //只有添加这个注释，才能把此属性添加到表中的字段
    private String name;
    @DatabaseField //只有添加这个注释，才能把此属性添加到表中的字段
    private String title;
    @DatabaseField //只有添加这个注释，才能把此属性添加到表中的字段
    private String body;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
