package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.google.gson.JsonObject;

import de.viadee.bpm.camunda.travelinsuranceprocessapp.model.TravelData;

public class TravelWarningService {

    public static String checkTravelWarning(TravelData TravelData) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://travelwarning.api.bund.dev/" + TravelData.getDestination()))
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            // 解析JSON响应
            JSONObject jsonResponse = new JSONObject(response.body());
            // 假设返回的JSON对象包含一个名为"warning"的字段
            String warning = jsonResponse.optString("warning", "No warning found");

            return warning;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while fetching travel warning";
        }
    }

    
}
