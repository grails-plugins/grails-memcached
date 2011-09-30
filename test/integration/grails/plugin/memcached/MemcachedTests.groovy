package grails.plugin.memcached

import test.Thing

import com.googlecode.hibernate.memcached.MemcachedCacheProvider

class MemcachedTests extends GroovyTestCase {

	// Hibernate doesn't put during transactions
	static transactional = false

	def sessionFactory

	void testCacheProvider() {
		assertEquals MemcachedCacheProvider, sessionFactory.settings.regionFactory.cacheProvider.getClass()
	}

	void testCaching() {
		sessionFactory.statistics.statisticsEnabled = true

		String name = 'test' + System.currentTimeMillis()
		long id = new Thing(name: name).save(flush: true, failOnError: true).id

		sessionFactory.currentSession.clear()

		assertEquals 0, sessionFactory.statistics.secondLevelCacheHitCount
		assertNull sessionFactory.statistics.secondLevelCacheStatistics[Thing.name]

		assertNotNull Thing.get(id)
		assertEquals 1, sessionFactory.statistics.secondLevelCacheHitCount
		assertNotNull sessionFactory.statistics.secondLevelCacheStatistics[Thing.name]

		sessionFactory.currentSession.clear()
		assertNotNull Thing.get(id)
		assertEquals 2, sessionFactory.statistics.secondLevelCacheHitCount
		assertNotNull sessionFactory.statistics.secondLevelCacheStatistics[Thing.name]
	}

	@Override
	protected void tearDown() {
		super.tearDown()
		Thing.list().each { it.delete() }
		sessionFactory.evict Thing
	}
}
