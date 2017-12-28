package InterfaceTestUtils;

import org.apache.http.conn.ssl.TrustStrategy;

public class AnyTrustStrategy implements TrustStrategy {

	@Override
	public boolean isTrusted(java.security.cert.X509Certificate[] chain, String authType)
			throws java.security.cert.CertificateException {
		// TODO Auto-generated method stub
		return true;
	}
	
}
