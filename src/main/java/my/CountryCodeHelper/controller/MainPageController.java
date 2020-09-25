package my.CountryCodeHelper.controller;

import my.CountryCodeHelper.model.User;
import my.CountryCodeHelper.repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {
    @Autowired
    private UsersRepo usersRepo;

    @GetMapping("/")
    public String index(Model model) {
        User user = usersRepo.findById(1L).isPresent() ? usersRepo.findById(1L).get() : null;
        model.addAttribute("username", user == null ? "userNotFound" : user.getName());
        return "index";
    }
}
