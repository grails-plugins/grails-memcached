grails.doc.authors = 'Burt Beckwith'
grails.doc.license = 'Apache License 2.0'
grails.doc.title = 'Memcached Plugin'

environments {
	test {
		grails {
			plugin {
				memcached {
					hosts = 'localhost:11211'
//					username = ''
//					password = ''
				}
			}
		}
	}
}

log4j = {
	debug 'com.googlecode.hibernate.memcached'
}
