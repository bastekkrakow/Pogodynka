package pl.splon.Pogodynka.models.services;

import org.json.JSONArray;
import org.json.JSONObject;
import pl.splon.Pogodynka.models.WeatherModel;
import pl.splon.Pogodynka.models.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class WeatherService {
    private static WeatherService ourInstance = new WeatherService();

    public static WeatherService getInstance() {
        return ourInstance;
    }

    private WeatherService() {
    }

    public List<WeatherModel> getWeather(final String cityName){
        String websiteResponse = Utils.readWebsiteContent("http://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=25e17e96a4a9b0756f4d9d25ead07c56");
        String description = null;
        int temperature;
        int pressure;
        int humidity;
        int clouds;
        LocalDate date=null;
        List<WeatherModel> weatherModels=new ArrayList<>();

        JSONObject root = new JSONObject(websiteResponse);
        JSONArray weatherObject = root.getJSONArray("weather");
        for (int i = 0; i < weatherObject.length(); i++) {
            JSONObject elementInArray = weatherObject.getJSONObject(i);
            description = elementInArray.getString("main");
        }

        JSONObject mainObject = root.getJSONObject("main");
        temperature = ((int) mainObject.getFloat("temp"))-273;
        pressure = mainObject.getInt("pressure");
        humidity = mainObject.getInt("humidity");

        JSONObject cloudsObject = root.getJSONObject("clouds");
        clouds = cloudsObject.getInt("all");
        date= LocalDate.now();

        weatherModels.add(
                new WeatherModel.Builder(cityName)
                        .setWeatherComment(description)
                        .setClouds(clouds)
                        .setHumidity(humidity)
                        .setPressure(pressure)
                        .setTemperature(temperature)
                         .setDate(date)
                        .build()
        );
        System.out.println(weatherModels.get(0));
        return weatherModels;
    }
    private List<WeatherModel> getForecast(String cityName) {
        String websiteResponse = Utils.readWebsiteContent("http://api.openweathermap.org/data/2.5/forecast?q=" + cityName + "&mode=json&appid=25e17e96a4a9b0756f4d9d25ead07c56");
        String description = null;
        int temperature;
        int humidity;
        int pressure;
        int clouds;

        List<WeatherModel> weatherModelList = new ArrayList<>();
        JSONObject root = new JSONObject(websiteResponse);

        JSONArray weatherList = root.getJSONArray("list");
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;
        LocalDate date = null;
        for (int i = 0; i < weatherList.length(); i++) {
            jsonObject = weatherList.getJSONObject(i);
            JSONObject main = jsonObject.getJSONObject("main");
            temperature = ((int) main.getFloat("temp")) - 273;
            humidity = main.getInt("humidity");
            pressure = (int) main.getFloat("pressure");
            clouds = jsonObject.getJSONObject("clouds").getInt("all");
            date = LocalDate.parse(jsonObject.getString("dt_txt").substring(0, 10));
            description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");

            weatherModelList.add(new WeatherModel.Builder(cityName)
                    .setTemperature(temperature)
                    .setHumidity(humidity)
                    .setPressure(pressure)
                    .setClouds(clouds)
                    .setWeatherComment(description)
                    .setDate(date)
                    .build());
        }
        return weatherModelList;
    }

    public List<WeatherModel> getWeatherForecast(String cityName) {
        List<WeatherModel> weatherModels=new ArrayList<>();
        List<WeatherModel> weatherModelList=getForecast(cityName);
        int temperature;
        int humidity;
        int pressure;
        int clouds;
        LocalDate date=null;
        String description=null;
        Map<LocalDate, List<WeatherModel>> weatherMap=weatherModelList.stream().collect(Collectors.groupingBy(s -> s.getDate()));
        for (Map.Entry<LocalDate, List<WeatherModel>> localDateListEntry : weatherMap.entrySet()) {
            description=localDateListEntry.getValue().get(0).getWeatherComment();
            date=localDateListEntry.getValue().get(0).getDate();
            temperature=(int)localDateListEntry.getValue().stream().mapToInt(s->s.getTemperature()).average().orElse(0);
            humidity=(int)localDateListEntry.getValue().stream().mapToInt(s->s.getHumidity()).average().orElse(0);
            pressure=(int)localDateListEntry.getValue().stream().mapToInt(s->s.getPressure()).average().orElse(0);
            clouds=(int)localDateListEntry.getValue().stream().mapToInt(s->s.getClouds()).average().orElse(0);
            weatherModels.add(new WeatherModel.Builder(cityName)
                    .setTemperature(temperature)
                    .setHumidity(humidity)
                    .setPressure(pressure)
                    .setClouds(clouds)
                    .setWeatherComment(description)
                    .setDate(date)
                    .build());
        }
        weatherModels.sort((o1,o2)->o1.getDate().compareTo(o2.getDate()));
        return weatherModels;
    }

}
