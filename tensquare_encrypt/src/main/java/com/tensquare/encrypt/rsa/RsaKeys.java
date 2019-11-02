package com.tensquare.encrypt.rsa;

/**
 * rsa加解密用的公钥和私钥
 * @author Administrator
 *
 */
public class RsaKeys {

	//生成秘钥对的方法可以参考这篇帖子
	//https://www.cnblogs.com/yucy/p/8962823.html

//	//服务器公钥
//	private static final String serverPubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6Dw9nwjBmDD/Ca1QnRGy"
//											 + "GjtLbF4CX2EGGS7iqwPToV2UUtTDDemq69P8E+WJ4n5W7Iln3pgK+32y19B4oT5q"
//											 + "iUwXbbEaAXPPZFmT5svPH6XxiQgsiaeZtwQjY61qDga6UH2mYGp0GbrP3i9TjPNt"
//											 + "IeSwUSaH2YZfwNgFWqj+y/0jjl8DUsN2tIFVSNpNTZNQ/VX4Dln0Z5DBPK1mSskd"
//											 + "N6uPUj9Ga/IKnwUIv+wL1VWjLNlUjcEHsUE+YE2FN03VnWDJ/VHs7UdHha4d/nUH"
//											 + "rZrJsKkauqnwJsYbijQU+a0HubwXB7BYMlKovikwNpdMS3+lBzjS5KIu6mRv1GoE"
//											 + "vQIDAQAB";
//
//	//服务器私钥(经过pkcs8格式处理)
//	private static final String serverPrvKeyPkcs8 = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDoPD2fCMGYMP8J"
//				 								 + "rVCdEbIaO0tsXgJfYQYZLuKrA9OhXZRS1MMN6arr0/wT5YniflbsiWfemAr7fbLX"
//				 								 + "0HihPmqJTBdtsRoBc89kWZPmy88fpfGJCCyJp5m3BCNjrWoOBrpQfaZganQZus/e"
//				 								 + "L1OM820h5LBRJofZhl/A2AVaqP7L/SOOXwNSw3a0gVVI2k1Nk1D9VfgOWfRnkME8"
//				 								 + "rWZKyR03q49SP0Zr8gqfBQi/7AvVVaMs2VSNwQexQT5gTYU3TdWdYMn9UeztR0eF"
//				 								 + "rh3+dQetmsmwqRq6qfAmxhuKNBT5rQe5vBcHsFgyUqi+KTA2l0xLf6UHONLkoi7q"
//				 								 + "ZG/UagS9AgMBAAECggEBANP72QvIBF8Vqld8+q7FLlu/cDN1BJlniReHsqQEFDOh"
//				 								 + "pfiN+ZZDix9FGz5WMiyqwlGbg1KuWqgBrzRMOTCGNt0oteIM3P4iZlblZZoww9nR"
//				 								 + "sc4xxeXJNQjYIC2mZ75x6bP7Xdl4ko3B9miLrqpksWNUypTopOysOc9f4FNHG326"
//				 								 + "0EMazVaXRCAIapTlcUpcwuRB1HT4N6iKL5Mzk3bzafLxfxbGCgTYiRQNeRyhXOnD"
//				 								 + "eJox64b5QkFjKn2G66B5RFZIQ+V+rOGsQElAMbW95jl0VoxUs6p5aNEe6jTgRzAT"
//				 								 + "kqM2v8As0GWi6yogQlsnR0WBn1ztggXTghQs2iDZ0YkCgYEA/LzC5Q8T15K2bM/N"
//				 								 + "K3ghIDBclB++Lw/xK1eONTXN+pBBqVQETtF3wxy6PiLV6PpJT/JIP27Q9VbtM9UF"
//				 								 + "3lepW6Z03VLqEVZo0fdVVyp8oHqv3I8Vo4JFPBDVxFiezygca/drtGMoce0wLWqu"
//				 								 + "bXlUmQlj+PTbXJMz4VTXuPl1cesCgYEA6zu5k1DsfPolcr3y7K9XpzkwBrT/L7LE"
//				 								 + "EiUGYIvgAkiIta2NDO/BIPdsq6OfkMdycAwkWFiGrJ7/VgU+hffIZwjZesr4HQuC"
//				 								 + "0APsqtUrk2yx+f33ZbrS39gcm/STDkVepeo1dsk2DMp7iCaxttYtMuqz3BNEwfRS"
//				 								 + "kIyKujP5kfcCgYEA1N2vUPm3/pNFLrR+26PcUp4o+2EY785/k7+0uMBOckFZ7GIl"
//				 								 + "FrV6J01k17zDaeyUHs+zZinRuTGzqzo6LSCsNdMnDtos5tleg6nLqRTRzuBGin/A"
//				 								 + "++xWn9aWFT+G0ne4KH9FqbLyd7IMJ9R4gR/1zseH+kFRGNGqmpi48MS61G0CgYBc"
//				 								 + "PBniwotH4cmHOSWkWohTAGBtcNDSghTRTIU4m//kxU4ddoRk+ylN5NZOYqTxXtLn"
//				 								 + "Tkt9/JAp5VoW/41peCOzCsxDkoxAzz+mkrNctKMWdjs+268Cy4Nd09475GU45khb"
//				 								 + "Y/88qV6xGz/evdVW7JniahbGByQhrMwm84R9yF1mNwKBgCIJZOFp9xV2997IY83S"
//				 								 + "habB/YSFbfZyojV+VFBRl4uc6OCpXdtSYzmsaRcMjN6Ikn7Mb9zgRHR8mPmtbVfj"
//				 								 + "B8W6V1H2KOPfn/LAM7Z0qw0MW4jimBhfhn4HY30AQ6GeImb2OqOuh3RBbeuuD+7m"
//				 								 + "LpFZC9zGggf9RK3PfqKeq30q";

	//服务器公钥
	private static final String serverPubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuYJg6RntwCSYJw9e2/MW\n" +
			"7YYapwviiV6CGmq2olYhXw6Ft/m4xiQAKMNoxvhHU1SoTykQnIwS74b1x6MQvfEF\n" +
			"Usih36bfyLJTUw8/HYROE6OLWGWxOBkyQv+mzA1KSpnLAVqwfFYyPuE8Dbkq9we/\n" +
			"uBroJerk91U4oLLdN/vjGrG54E3YH/CKdSeTFBe8dGmkKwl6jYpcM6KTSIbiSMYL\n" +
			"ScfeoCa4wocDQo8k+OjsOOSRgn33k5iKGyFRfP5DQYQfF9TBmgTOdfx7T4ybHmp3\n" +
			"H+DdVgPALYJGTGheJbMrukeepV7wa9Knq3q6+KvUFszZTTVKXadmEv4svN+zuSkl\n" +
			"VQIDAQAB";

	//服务器私钥(经过pkcs8格式处理)
	private static final String serverPrvKeyPkcs8 = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC5gmDpGe3AJJgn\n" +
			"D17b8xbthhqnC+KJXoIaaraiViFfDoW3+bjGJAAow2jG+EdTVKhPKRCcjBLvhvXH\n" +
			"oxC98QVSyKHfpt/IslNTDz8dhE4To4tYZbE4GTJC/6bMDUpKmcsBWrB8VjI+4TwN\n" +
			"uSr3B7+4Gugl6uT3VTigst03++MasbngTdgf8Ip1J5MUF7x0aaQrCXqNilwzopNI\n" +
			"huJIxgtJx96gJrjChwNCjyT46Ow45JGCffeTmIobIVF8/kNBhB8X1MGaBM51/HtP\n" +
			"jJseancf4N1WA8AtgkZMaF4lsyu6R56lXvBr0qererr4q9QWzNlNNUpdp2YS/iy8\n" +
			"37O5KSVVAgMBAAECggEAa3JnvTvmaB8J/b26XL0s74B4GX8ZxCT4SGK8y8QFajBY\n" +
			"JRo/+ovJh9K+8lfFwC9NmSQY2gOLyC6sVuhM/ycFxVAt6gOWpIEdIAO5s9f4/QkB\n" +
			"4b5s6ig143Z93rxn5lOzsGIdzLaEPcVpai1xBEn5sqHrCmNGITe8J9/Rn+aGQOPp\n" +
			"GiCDJjh7Vy6OhPJGdzo9SVlil+KsZ47+Fq8vLQC1DgQgS/fZ2s+c+FCB3KEEt6Wy\n" +
			"peBmzaRUB7Yul1UISWUAMR9UW1O4wlVJrltvJExbWO+i1N+Dj77aZZLQXZ93tEFm\n" +
			"pbHOdEN9766/ZcdRUK3MKmRKRi5KiqloegsyzliblQKBgQDlDJm6JCG796CgM5cq\n" +
			"kNVwjP7hq0UVJ15FohQSOldun7+OE4NBQLDedNQypP9NWU0SAUDOIev2kbv3xqXJ\n" +
			"ytZx4TbdB6AhNKuFc+BroIsbl2fTe1EqoWtyT9bg7Ik9fwm0uY1f8gbaj/pSqN86\n" +
			"KoU0Vi6Ek3u7lgKRl8s0d/YLlwKBgQDPVkW2yy2xTB0aEErdGk18b7Yl0hU7AFjm\n" +
			"+SEpoWhOSutAi4EpItQ1rYtKnTcYPilpbW5cxfKfeTjZx5YLSe3j4V4aj5Ld3csA\n" +
			"ecmlryX9uG4rJYVxXrm2CHhjUPHWab3pOdMkN/zDHIkbWd0FxmJHKOOvapNb0YHY\n" +
			"xrtmy/2j8wKBgQDKwbK7ggyStgvshH7GYVtvvIBMCC6pISZkgas+z4JiHOuWu1Mv\n" +
			"SjLHWagWBNUzBWAiFhXl40VcCXnZRnGwBex69qB4XK21d0h2ZDM/UuiRTh/Mp5cc\n" +
			"I7RXhrFyfjCmzXQsFS0+x6Kli1pOHcstVEKCJ+AZ3+xiC9LOSw7IrYCSlQKBgQCW\n" +
			"OGCcNwttCMORDbrgBV2cyeeD8XlWXUMMXfZMDljfWOKnyiuTnVt5ZLqmHdA8LVWd\n" +
			"wB6U8wp9xruM9lFc7nyAfdnLjriPLVIWjyatd4AkGwsfEblkUYhmPi7TbmM5wTVK\n" +
			"lI3JERN+Xd9sxKghtgGgoxyMDMClZt8YK6y9pxCnRwKBgHGhf4mWJRUER+0Br8gV\n" +
			"gEQZHpPR43zchFY6PFUiq2QVY12cSg0Os03LjVOsRb7MkZ+Kt/bDHpLwZdF98vfd\n" +
			"8a8VSmcAVm3cHUB/77nmDTj2A56ItBjBTWuGGBlfIe/mDHCfJgFkxNR3oRitu9/N\n" +
			"K9x1u+oFHuvJq2A5LpyGmEw8";

	public static String getServerPubKey() {
		return serverPubKey;
	}

	public static String getServerPrvKeyPkcs8() {
		return serverPrvKeyPkcs8;
	}
	
}
