package com.github.guokaia.mekatok.notice.service;

import com.github.guokaia.mekatok.context.MekatokApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * @author <a href="mailto:guokai0727@gmail.com">GuoKai</a>
 * @date 2022/2/5
 */
@SpringCloudApplication
public class NoticeApplication {
    public static void main(String[] args) {
        MekatokApplication.run(NoticeApplication.class, args);
    }
}
