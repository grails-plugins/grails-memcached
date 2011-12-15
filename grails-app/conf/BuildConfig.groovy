grails.project.work.dir = 'target'
grails.project.docs.output.dir = 'docs/manual' // for backwards-compatibility, the docs are checked into gh-pages branch

grails.project.dependency.resolution = {
	inherits 'global'
	log 'warn'

	repositories {
		grailsPlugins()
		grailsHome()
		grailsCentral()

		mavenLocal()
		mavenRepo 'http://raykrueger.googlecode.com/svn/repository' // for hibernate-memcached
		mavenRepo 'http://files.couchbase.com/maven2/'
		mavenCentral()
	}

	dependencies {
		compile('com.googlecode:hibernate-memcached:1.3') {
//			transitive = false
			excludes 'spymemcached', 'java_memcached', 'slf4j-api', 'slf4j-log4j12',
			         'hibernate', 'hibernate-annotations', 'junit', 'hsqldb', 'groovy-all'
		}
		compile('spy:spymemcached:2.7.3') {
//			transitive = false
			excludes 'netty', 'jettison', 'commons-codec'
		}
	}

	plugins {
		compile(":hibernate:$grailsVersion") { export = false }
		build(':release:1.0.0') { export = false }
	}
}
