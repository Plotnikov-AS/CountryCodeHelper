package my.CountryCodeHelper.controller;

import my.CountryCodeHelper.model.User;
import my.CountryCodeHelper.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {
    @Autowired
    private UserRepo userRepo;

    @GetMapping("/")
    public String index(Model model) {
        User user = userRepo.findById(1L).isPresent() ? userRepo.findById(1L).get() : null;
        model.addAttribute("username", user == null ? "userNotFound" : user.getName());
        return "index";
    }
}
