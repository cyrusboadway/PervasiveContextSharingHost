package test;

import java.net.InetSocketAddress;

import ca.queensu.pervasive.client.Client;
import ca.queensu.pervasive.common.ClientRegistrationRequest;
import ca.queensu.pervasive.common.ClientRegistrationResponse;
import ca.queensu.pervasive.common.HostKeyRequest;
import ca.queensu.pervasive.common.HostKeyResponse;
import ca.queensu.pervasive.common.LetterOfMark;
import ca.queensu.crypto.Codec;
import ca.queensu.xml.XMLHandler;

public class RegisterClientTest {
	
	public static void registerDevice1(InetSocketAddress a) throws Exception{
		HostKeyResponse keyResponse = (HostKeyResponse) Client.sendMessage(new HostKeyRequest(), a); 
		String hostPublicKey = keyResponse.key(); 			

		//Create letter of mark
		String pub = "C86VC0q62Ig6I8Rt3G410GK00u6D0321YGA1WG2cJubc8cplMcx/AAi5z94azc1nsHgqbxI.CuRdSMTLEk9r43z/gMWIYo9qrb40URb6I7LyqxZ4FLEkVg0Yd.41pcn6MlvRZQcoQoFijHY7VDHeLvoSAX2dRBzN5MKdpxtEPmTGlFp9ne/5Al1zujT31CRVyRcgOfyG9m18UkTHrm830G01";
		String pri = "C882TG81030D1WagXaY6zmq10G4500I20bymWW9R0W400e610APFYMOYREzQRlyegmNqaIJsO77P6hINjBupXkTnPrKwudKGFt.fQ1AB8dJMKG1vkKP8TNpJkCGzKwv.e2AVuG7ER4PQ/bkDgR9h8.or68Tyr6XNd9mg4ATilrSLPIVFlSvd1r2y/Cd6ZyKgy7tYrqC4nj/nkQfYdn0d04Xwvr7N0WC10042WO1lEcoZKKpN./oMJ6MjKlShw5ToRGWELXFypKjXIHWQ2OhXJX.PolmUmiqbfHo1c.TCsJCoR0jxkwHMvBj6pFJNWahc8V/AeW1PR0wazGOycCD1hx16.CqQ/oSANy18ybav8h6N0TDMvEMTuB/DM974SmTk0Gf4jfzdmkswX/A7kG910E7k1x3FV8NnrilUE5dwis44MXFIZ.GarOho5PSDeY6w.9OTfgXEkc2.OVJ32tLCrxPT/8dDfxiWdjIUE2Ea8di2GG2ySYFwnpRGs84aZu6cJhNxbSQ3Nj9pMLcVJE72toyLH2rSZK63YUAGL2g3lwIIm2SOJOzJsXg/MARHv7jfjErL0a0GYNYyesDCzfUlKdEpUbiuvRMcByfYUwEvoE1rhmJ83KHsq7BBRF/DPm8AXM18LBYHMwt9T6ahb4QhxspIXmWJ0a0iUup.htF33BW/LnYrbpBXzbBhmy1DjZElfCXDvRq9UXHp6cpoZUFBIVBNCg.uR5zE7r3Bx5eF/Eypy8afHAeP0a1HYeNBjf/q.cquiwcUy5zC/OeRlIbR0F3KgC/BuMOk9KT9JsmwO9NNEnh8MyCxGhvscfurJzeLOKnsCLldXJlJ";
		LetterOfMark lom = new LetterOfMark(pub, hostPublicKey, 1272996865);
		lom.setSignature(Codec.sign(lom.fingerprint(), pri));
		
		//Prepare device to be registered.
		String xml = XMLHandler.readFile("/home/cyrus/Documents/workspace/Host/src/xml/device1.xml");
		xml = Codec.encrypt(xml, hostPublicKey);
		//System.out.println(xml);

		ClientRegistrationResponse res = (ClientRegistrationResponse)Client.sendMessage(new ClientRegistrationRequest(xml,lom),a);
		if(res.success())
			System.out.println("Successfully registered client.");
		else
			System.out.println("viewFailed to register client.");
	}
	
	public static void registerPerson1(InetSocketAddress a) throws Exception{
		HostKeyResponse keyResponse = (HostKeyResponse) Client.sendMessage(new HostKeyRequest(),a); 
		String hostPublicKey = keyResponse.key();

		//Create letter of mark
		String pub = "C86VC0q62Ig6I8Rt3G410GK00u6D0321YGA1WG2.xiLajy/YglWPxTTWT3JRu0wzjuNKxdJ9SG7ZHRlkzy.IpiU4BLJPjNMCg7pMZRXjhjikyoJxdeQFj25au8DaoEuk8hTHBAzCPA2L76mPb/xmfDNo5p8fHX3DwE6kzqcdfJ2d.R3kjA8ROkqwoPkGWKXSIYQpo3XvqRUZE.PkuG830G01";
		String pri = "mWW9s0W40C0q62Ig6I8Rt3G410GK01882O3220bm20G02WO40lkx5PBVFughu6UtNO7Gqs.0ElRU5rEvqoN41uqMxxlVFaix7X2rKsRLrZAXyresuRQxRBlCa.vw6ZxGXPE23PCZkBYAtKIolJ6IWbHni6PV.yAJLyXSoAKOGpUZXhlT9fwKmf/cmxhIY6sBjEicRa858N4eciyWuUT6teplcRk420m400GA1WG2WdeJu0PcaUMTLYBlIh6zxEBMjww5kIrjxoj5dkzOfPTRz3MxIHi16bLnQ0xDXMhztfSr5PSuApeKiXi10.8uEsO.V7vxtRGuKbJ6b/vU5ItmFhJw3QfruHAeU3t2BoeuRdCxcd1pfEc2WQkvsJO1vuztmGa3.6Oa4o82d30ikSG910F3kzpNTB/cYbS7FNz/aUnsLdwISdeoVHIWUBuUiHAeBfD9EHKLD1J.Iner37059fEubZo/sBer8T4unjVUfVjy2GG3AtrkEJNwfPCWdVoTCx5Rly5cqiOWPS1RUPNSMtM.6Q.eu0OI3O7DsOtcqZP4VBGHijJmYQMsc2Nvc/CWAj8e/0a038TWivnSyavI6r7zMbStJyA86hTAHsN0ukKkyuynmkNu73ULmfJEmG.GuwUflITdF3iNnnQsDGAVYR5i5GJhP0a0IsKU/O/7LkJXZUmlYkBwd4bIpLXz81SCABB4mtQW3/3Z9jL9ZXcsJtL0iTqwvRynESLH1.Zkcnf48xec3VHol0a1uu/dMxKlRkAch5ETzw40hGMYUoG.2MApp6X59nM2TYcDsfGCJ47I.BpAXA5AsbEYYKW3QFQ3g4EwKzG6EErm3";
		LetterOfMark lom = new LetterOfMark(pub, hostPublicKey, 1272996865);
		lom.setSignature(Codec.sign(lom.fingerprint(), pri));
		
		//Prepare device to be registered.
		String xml = XMLHandler.readFile("/home/cyrus/Documents/workspace/Host/src/xml/person1.xml");
		xml = Codec.encrypt(xml, hostPublicKey);
		//System.out.println(xml);

		ClientRegistrationResponse res = (ClientRegistrationResponse)Client.sendMessage(new ClientRegistrationRequest(xml,lom),a );
		if(res.success())
			System.out.println("Successfully registered client.");
		else
			System.out.println("Failed to register client.");
	}
	
	public static void main(String[] args) throws Exception{
		InetSocketAddress a = new InetSocketAddress("localhost",4440);
		registerDevice1(a);
		registerPerson1(a);
	}

}