package com.hc9.common.constant;

/**
 * 环讯接口证书
 * 
 * @author RanQiBing 2013-01-03
 * 
 */
public interface Certificate {
    /**
     * 用户808805 测试
     */
    /** 1、MD5证书**/
    String MD5CCERTIFICATE05 = "GPhKt7sh4dxQQZZkINGFtefRKNPyAj8S00cgAwtRyy0ufD7"
            + "alNC28xCBKpa6IU7u54zzWSAv4PqUDKMgpOnM7fucO1wuwMi4RgPAnietmqYIhHX"
            + "Z3TqTGKNzkxA55qYH";
    /** 2、RSA公钥(IPS)**/
    /*
    String PUBLICKEY05 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC0SlytWpJJJ2fdI"
            + "RXi6obM6hQ/9eD4dUo4hzMsMoBDbsPkkdRdbfkyHOqr9aUKIjSbIehS8cwhI9XpSh"
            + "F0t+2uuunizEShjvD2HsGMxSSrE6GaxXtidg9wte9rnZzlaafAzHztThRLoELTE/"
            + "sx0og1HwxmaPSAy3/lm0LPjJNJZQIDAQAB";
	*/
    /**客户提供的RSA公钥(IPS)*/
    String PUBLICKEY05 ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMWwKD0u90z1K8Wv"
    		+ "tG6cZ3SXHLUqmQCWxbT6JURy5BVwgsTdsaGmr22HT4jfEBQHEjmTtyUWC5Ag9Cwg"
    		+ "ef0VFrDB7TqyhWfVA7n8SvV6b1eDbQlY/qhUb50+3SCpN7HxdPzdMDkJjy6i6syh"
    		+ "7RtH0QfoApHS6TLY4DjPvbGgdXhwIDAQAB";
    /** 3、3des密钥**/
    String DES_ALGORITHM05 = "ICHuQplJ0YR9l7XeVNKi6FMn";

    /** 4、3des向量**/
    String DESEDEVECTOR05 = "2EDxsEfp";
    /** 用户 **/
    String CERT02 = "808805";
    /**字符串截取长度**/
    int XMLTOP_INDEX = 39;
    /**字符串截取长度**/
    int XMLTOP_INDEXTWO = 38;
    
}
