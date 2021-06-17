package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model){
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form){

        //validation 부분은 생략함. 해보려면 해보시오.

        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
        //게터세터 다 날리고 생성자 만드는 것이 더 나은 설계임. 실무에서는 세터 다 날림.
        itemService.saveItem(book);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model){
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    //상품 수정 매우 중요. 변경감지와 병합 2가지 방법 있음. 사람들 관성적으로 병합(머지)씀

    @GetMapping(value = "/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
        Book item = (Book) itemService.findOne(itemId); //캐스팅 좋진 않지만 예제 단순화 위해 캐스팅 씀

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        //자동화 라이브러리도 장단이 있음. //ctrl+shift+alt+j
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form",form);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    public String updateItem(@PathVariable String itemId, @ModelAttribute("form") BookForm form){

        Book book = new Book();
        book.setId(form.getId()  );
        book.setName(form.getName()   );
        book.setPrice(form.getPrice()   );
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor()  );
        book.setIsbn(form.getIsbn()   );
        //권한 체크해주는 로직 나중에 있어야. 세션에 넣을 수도 있으나 요즘 세션객체 잘 안써서..
        itemService.saveItem(book);
        return "redirect:/items";

    }

}
