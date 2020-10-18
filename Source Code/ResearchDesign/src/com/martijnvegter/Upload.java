package com.martijnvegter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;

public class Upload {
	public static void main(String[] args) throws Exception {
		String token = "ffadd4a0d6c838810f31303056497289a20edd6d95ce74602f530c9891b30d08";
		String analyseId;
		String jvmId;

		File directoryPath = new File("D:\\School\\RD\\single");
		// List of all files and directories
		File filesList[] = directoryPath.listFiles();
		for (File file : filesList) {
			try (InputStream fileStream = new FileInputStream(file)) {
				processGCLogFile(token, file.getName(), file.getName(), file.getName(), fileStream);
			}
		}

//    org.apache.http.client.HttpClient hc = HttpClientBuilder.create().build();
//    HttpEntity file = MultipartEntityBuilder.create()
//            .addBinaryBody("gc.log", new FileInputStream(new File(fileName)),
//                    ContentType.TEXT_PLAIN, fileName).build();
//    HttpPost post = new HttpPost("http://localhost:3000/gc/jvm/log/process" + "?token=" + token + "&analyse_id=" + analyseId
//            + "&jvm_id=" + jvmId + "&sync=true");
//    post.setEntity(file);
//    HttpResponse response = hc.execute(post);
	}

	static void processGCLogFile(String token, String analyseId, String jvmId, String fileName, InputStream fileStream)
			throws IOException {
		org.apache.http.client.HttpClient hc = HttpClientBuilder.create().build();
		HttpEntity file = MultipartEntityBuilder.create()
				.addBinaryBody(fileName, fileStream, ContentType.TEXT_PLAIN, fileName).build();
		HttpPost post = new HttpPost("http://localhost:3000/gc/jvm/log/process" + "?token=" + token + "&analyse_id="
				+ analyseId + "&jvm_id=" + jvmId + "&sync=true");
		post.setEntity(file);
		HttpResponse rs = hc.execute(post);
		System.out.println(rs);
	}
}
