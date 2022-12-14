package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        if (item.getId() == null){
            em.persist(item);
        } else{
            em.merge(item); //merge의 정체?! ->실무에서 쓸 일이 거의 없다
            //머지는 updateItem 한땀 한땀 짠 코드를 jpa가 한번에 해주는 것.
            //파라미터로 넘어온 item은 영속성으로 바뀌진 않음.
        }
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        //여러건은 jpql 작성해야.
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
