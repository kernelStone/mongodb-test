package com.mongodb.test.bean;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.Date;

/***
 * @author zhulei
 * @date 2023/11/10 14:51
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "tg_game_user")
public class User {
    @Id
    @Indexed(unique = true)
    private String id;

    //@Field(value = "first_name")
    private String firstName;

    private String lastName;
    /**
     * 索引设置
     */
    @Indexed(unique = true)
    private String userName;

    private Integer status;

    private Date createDate;

    private Date modifyDate;
    /**
     * 乐关锁标识位
     */
    @Version
    private Long version;

    @Getter
    public enum Status {

        INIT(0),

        ;

        private int code;

        Status(int code) {
            this.code = code;
        }

        public static Status valueOf(int code) {
            for (Status type : values()) {
                if (code == type.code) {
                    return type;
                }
            }
            return null;
        }
    }
}
