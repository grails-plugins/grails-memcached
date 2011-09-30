grails.project.class.dir = 'target/classes'
grails.project.test.class.dir = 'target/test-classes'
grails.project.test.reports.dir = 'target/test-reports'
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
		runtime('com.googlecode:hibernate-memcached:1.3') { transitive = false }
		compile('spy:spymemcached:2.7.1') { transitive = false }
	}

	plugins {

		compile ":hibernate:$grailsVersion"

		build(':release:1.0.0.RC3') { export = false }
	}
}
