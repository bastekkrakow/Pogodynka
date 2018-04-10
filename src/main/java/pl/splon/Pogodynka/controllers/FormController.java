package pl.splon.Pogodynka.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.splon.Pogodynka.models.WeatherModel;
import pl.splon.Pogodynka.models.services.WeatherService;

import java.util.List;

@Controller
public class FormController {
    private WeatherService weatherService = WeatherService.getInstance();

    @GetMapping("/weather")
    public String weatherForm() {
        return "weather";
    }

    @PostMapping("/getWeatherController")
    public String weatherForm(@RequestParam("cityName") String cityName, @RequestParam("weatherType") String weatherType, Model model) {
        List<WeatherModel> weatherModel=null;
        weatherModel=weatherType.equals("actual") ? weatherService.getWeather(cityName) : weatherService.getWeatherForecast(cityName);
        model.addAttribute("weathers",weatherModel);
        return "weather";
    }
}
