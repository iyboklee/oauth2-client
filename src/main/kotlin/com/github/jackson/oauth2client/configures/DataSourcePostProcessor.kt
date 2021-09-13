package com.github.jackson.oauth2client.configures

import net.sf.log4jdbc.Log4jdbcProxyDataSource
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class DataSourcePostProcessor : BeanPostProcessor {

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        return if (bean is DataSource && bean !is Log4jdbcProxyDataSource) {
            Log4jdbcProxyDataSource(bean as DataSource?)
        } else {
            bean
        }
    }

}