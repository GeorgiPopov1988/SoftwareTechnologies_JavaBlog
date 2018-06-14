package softuniBlog.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import softuniBlog.bindingModel.ArticleBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.UserRepository;

@Controller
public class ArticleControler {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;


    public ArticleControler(UserRepository userRepository, ArticleRepository articleRepository) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
    }

    @GetMapping("/article/create")
    private String create(Model model){

        model.addAttribute("view", "article/create");
        return "base-layout";
    }

    @PostMapping("/article/create")
    private String createProcess(ArticleBindingModel model){

        UserDetails currentUser =
                (UserDetails) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user = this.userRepository.findByEmail(currentUser.getUsername());
        Article article = new Article(model.getTitle(), model.getContent(), user);
        user.getArticles().add(article);
        this.userRepository.save(user);
        this.articleRepository.saveAndFlush(article);

        return "redirect:/";
    }

    @GetMapping("/article/{id}")
    public String getArticleContent(Model model,@PathVariable("id") Integer id){

        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);
        model.addAttribute("article", article);
        model.addAttribute("view", "article/details");

        return "base-layout";

    }

    @GetMapping("/article/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model){

        if(!this.articleRepository.exists(id)){
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);
        model.addAttribute("article", article);
        model.addAttribute("view", "article/edit");

        return "base-layout";

    }

    @PostMapping("/article/edit/{id}")
    private String editProcess(ArticleBindingModel model, @PathVariable("id") Integer id){

        if(!this.articleRepository.exists(id)){
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);
        article.setContent(model.getContent());
        article.setTitle(model.getTitle());

        this.articleRepository.saveAndFlush(article);

        return "redirect:/article/" + article.getId();

    }

    @GetMapping("/article/delete/{id}")
    public String delete(@PathVariable("id") Integer id, Model model){

        if(!this.articleRepository.exists(id)){
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);
        model.addAttribute("article", article);
        model.addAttribute("view", "article/delete");

        return "base-layout";

    }

    @PostMapping("/article/delete/{id}")
    private String deleteProcess(ArticleBindingModel model, @PathVariable("id") Integer id){

        if(!this.articleRepository.exists(id)){
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);

        this.articleRepository.delete(article);

        return "redirect:/";

    }



}
