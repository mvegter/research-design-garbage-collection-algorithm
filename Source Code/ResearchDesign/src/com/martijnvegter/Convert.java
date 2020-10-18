package com.martijnvegter;

import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Convert {

	public static void main(String[] args) throws Exception {
		String token = "1e1dc3c8d24c87ecdf9921557c6de01d5c7c7403586f55029e36e5cc321c984e";

		URL url = new URL("http://localhost:3000/rest/analyse/all?&token=" + token);
		String data = IOUtils.toString(url, Charset.forName("UTF-8"));

		JSONObject result = new JSONObject(data).getJSONObject("result");
		JSONArray analyses = result.getJSONArray("analyses");
		for (Object object : analyses) {
			JSONObject analyse = ((JSONObject) object);

			String analyse_id = analyse.getString("id");
			JSONObject first_utc = analyse.getJSONObject("first_utc");
			JSONObject last_utc = analyse.getJSONObject("last_utc");
			JSONObject jvm_names = analyse.getJSONObject("jvm_names");
			for (String jvm_id : JSONObject.getNames(jvm_names)) {
				Long first = first_utc.getNumber(jvm_id).longValue();
				Long last = last_utc.getNumber(jvm_id).longValue();

				last = Math.max(first + 60 * 1000, last);

				URL urlData = new URL(String.format(
						"http://localhost:3000/rest/gc/jvm/events/full/sample/stream?token=%s&analyse_id=%s&jvm_id=%s&tz=Africa%%2FMonrovia&from=%d&to=%s&delimit=true&stats=true",
						token, analyse_id, jvm_id, first, last));
				String[] rawData = IOUtils.toString(urlData, Charset.forName("UTF-8")).split("\\$d");
				JSONObject jvmData = new JSONObject(rawData[rawData.length - 1]);

				JSONObject percentiles = jvmData.getJSONObject("percentiles");
				double p1 = Math.round((percentiles.getDouble("0.5") / 1000.0) * 100.0) / 100.0;
				double p2 = Math.round((percentiles.getDouble("0.9") / 1000.0) * 100.0) / 100.0;
				double p3 = Math.round((percentiles.getDouble("0.95") / 1000.0) * 100.0) / 100.0;
				double p4 = Math.round((percentiles.getDouble("0.99") / 1000.0) * 100.0) / 100.0;
				double p5 = Math.round((percentiles.getDouble("0.999") / 1000.0) * 100.0) / 100.0;

				double allocation_rate = Math.round((jvmData.getInt("allocation_rate") / 1024.0) * 100.0) / 100.0;
				double promotion_rate = Math.round((jvmData.getInt("promotion_rate") / 1024.0) * 100.0) / 100.0;

				Long totalTime = last_utc.getNumber(jvm_id).longValue() - first_utc.getNumber(jvm_id).longValue();
				Long gcTime = 0L;
				for (String generationId : JSONObject.getNames(jvmData.getJSONObject("generation_stats"))) {
					gcTime += jvmData.getJSONObject("generation_stats").getJSONObject(generationId)
							.getLong("pause_time");
				}

				double throughput = Math.round(((100 - (gcTime / totalTime) / 10.0) / 100.0) * 100.0) / 100.0;

				System.out.println(String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s", jvm_names.getString(jvm_id),
						throughput, p1, p2, p3, p4, p5, allocation_rate, promotion_rate));
			}
		}
	}
}
