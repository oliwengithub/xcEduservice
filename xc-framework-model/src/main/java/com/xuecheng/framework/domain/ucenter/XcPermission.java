package com.xuecheng.framework.domain.ucenter;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by admin on 2018/3/19.
 */
@Data
@NoArgsConstructor
@ToString
@Entity
@Table(name="xc_permission")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class XcPermission {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;
    @Column(name="role_id")
    private String roleId;
    @Column(name="menu_id")
    private String menuId;
    @Column(name="create_time")
    private Date createTime;


}
