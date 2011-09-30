/* Copyright 2011 SpringSource.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugin.memcached

import grails.util.GrailsUtil
import net.spy.memcached.BinaryConnectionFactory

import org.apache.log4j.Logger
import org.springframework.beans.PropertyValue
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.core.Ordered

import com.googlecode.hibernate.memcached.MemcachedCacheProvider

/**
 * @author Burt Beckwith
 */
class MemcachedBeanPostprocessor implements BeanDefinitionRegistryPostProcessor, Ordered {

	protected Logger log = Logger.getLogger(getClass())

	// later than CF and Heroku plugins so they can adjust the config from the environment first
	int getOrder() { 150 }

	/**
	 * {@inheritDoc}
	 * @see org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry(
	 * 	org.springframework.beans.factory.support.BeanDefinitionRegistry)
	 */
	void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
		log.info 'postProcessBeanDefinitionRegistry'
	}

	/**
	 * {@inheritDoc}
	 * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(
	 * 	org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
	 */
	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {

		log.info 'postProcessBeanFactory start'

		def appConfig = beanFactory.parentBeanFactory.getBean('grailsApplication').config

		try {
			if (beanFactory.containsBean('dataSource')) {
				fixMemcached beanFactory, appConfig
			}
		}
		catch (Throwable e) {
			handleError e, 'Problem updating DataSource'
		}
	}

	protected void fixMemcached(ConfigurableListableBeanFactory beanFactory, appConfig) {

		def memcachedConfig = appConfig.grails.plugin.memcached
		String hosts = getConfigString(memcachedConfig, 'hosts')

		if (!hosts) {
			return
		}

		String username = getConfigString(memcachedConfig, 'username')
		String password = getConfigString(memcachedConfig, 'password')

		BeanDefinition beanDefinition = beanFactory.getBeanDefinition('hibernateProperties')
		PropertyValue propertyValue = beanDefinition.getPropertyValues().getPropertyValue('properties')
		Map properties = propertyValue.getValue()

		properties['hibernate.memcached.servers'] = hosts
		if (username) properties['hibernate.memcached.username'] = username
		if (password) properties['hibernate.memcached.password'] = password
		properties['hibernate.cache.provider_class'] = MemcachedCacheProvider.name
		properties['hibernate.memcached.connectionFactory'] = BinaryConnectionFactory.simpleName

		log.debug "Updated Memcached from config: $memcachedConfig"
	}

	protected String getConfigString(config, String name) {
		def value = config[name]
		return value instanceof String ? value : null
	}

	protected void handleError(Throwable t, String prefix) {
		GrailsUtil.deepSanitize t
		log.error "$prefix: $t.message", t
	}
}
