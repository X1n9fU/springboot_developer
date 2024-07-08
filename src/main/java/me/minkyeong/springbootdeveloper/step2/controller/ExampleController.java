package me.minkyeong.springbootdeveloper.step2.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller //컨트롤러라는 것을 명시적으로 표시
public class ExampleController {

    @GetMapping("/thymeleaf/example")
    public String thymeleafExample(Model model){ //Model : HTML(뷰) 쪽으로 값을 넘겨주는 객체
        Person examplePerson = new Person();
        examplePerson.setId(1L);
        examplePerson.setName("A");
        examplePerson.setAge(11);
        examplePerson.setHobbies(List.of("운동","독서"));

        model.addAttribute("person", examplePerson); //모델에 값을 저장 (Person 객체 저장)
        model.addAttribute("today", LocalDate.now());

        return "example"; //view의 이름 (example.html)
    }

    @Getter
    @Setter
    class Person{
        private Long id;
        private String name;
        private int age;
        private List<String> hobbies;
    }
}
