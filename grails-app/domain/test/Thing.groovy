package test

class Thing {

	String name

	int hashCode() {
		5
//		int code = name ? name.hashCode() : 0
//		println "\nHC $name $code\n"
//		code
	}

	boolean equals(other) {
		name == other?.name
	}

	static mapping = {
		cache true
	}
}
