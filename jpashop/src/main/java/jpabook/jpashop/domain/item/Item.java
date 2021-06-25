package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.domain.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
//import jpabook.jpashop.domain.Category;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@BatchSize(size = 100) //컬렉션 아닌 경우 여기다가 배치사이즈 추가 가능
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<Category>();

    //==비즈니스 로직==//
    /*
    * stock 증가
    * */
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }
    /*
    * stock 감소
    * */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
    //가장 객체지향적으로.. 데이터 접근하는 곳에서 에드스톡, 리무브스톡 해주는

}