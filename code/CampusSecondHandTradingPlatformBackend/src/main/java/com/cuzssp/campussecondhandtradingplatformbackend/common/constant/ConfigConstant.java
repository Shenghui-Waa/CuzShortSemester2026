package com.cuzssp.campussecondhandtradingplatformbackend.common.constant;

import java.time.LocalDateTime;

/*
  配置信息常量
 */
public class ConfigConstant {

    public enum StorageSupport {
        R2{
            @Override
            public String toString() {
                return super.toString().toLowerCase();
            }
        },
        OSS{
            @Override
            public String toString() {
                return super.toString().toLowerCase();
            }
        }
    }

}
