package ibm.bpm.soap.ws.invoker;

import com.ibm.bpm.logging.LoggerHelper;
import com.ibm.bpm.ws.soap.SOAPMessageConnectorDelegate;
import com.lombardisoftware.logger.WLELoggerConstants;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.logging.Logger;
import javax.xml.rpc.ServiceException;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SOAP_CLIENT {

	private SOAP_CLIENT() {
		super();
	}

	public static final String DEFAULT_CHARSET = "UTF-8";
	private static final Logger LOGGER = Logger.getLogger(SOAP_CLIENT.class.getName());
	
	public String callWebService(String wsdl, String serviceNS, String serviceName, String address,
			String inputEnvelope) throws MalformedURLException, ServiceException, SAXException, RemoteException {
		return callWebService(wsdl, serviceNS, serviceName, address, inputEnvelope, null);
	}

	public String callWebService(String wsdl, String serviceNS, String serviceName, String address,
			String inputEnvelope, String soapAction)
			throws MalformedURLException, ServiceException, SAXException, RemoteException {
		return callWebService(wsdl, serviceNS, serviceName, address, inputEnvelope, soapAction, null, null);
	}

	public String callWebService(String wsdl, String serviceNS, String serviceName, String address,
			String inputEnvelope, String soapAction, String username, String password)
			throws MalformedURLException, ServiceException, SAXException, RemoteException {
		SOAPMessageConnectorDelegate smcd = new SOAPMessageConnectorDelegate();
		return smcd.callWebService(wsdl, serviceNS, serviceName, address, inputEnvelope, soapAction, username, password,
				"UTF-8");
	}

	private static void disableSslVerification() {
		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return new java.security.cert.X509Certificate[0];
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
					// Do nothing
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
					// Do nothing
				}
			} };

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		} catch (KeyManagementException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	static {
		disableSslVerification();
	}
}