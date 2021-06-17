package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional //read only서 저장 안 되니까 여기서 써줘야함. //가까운 곳 우선권이라서
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity ){ //Book param){
        //파라미터가 많다면 dto를 만들어서 푸는 것도 좋은 방법이다.
        //->예를 들면 String name, int price, int stockQuantity 대신에 updateItemDto를 사용
        Item findItem = itemRepository.findOne(itemId);

        //findItem.change(price,name,stockQuantity); //->이런 식으로 의미있는 메써드를 넣어놔야 역추적하면서
        // 아 여기서 변경되는구나 알 수 있음
        //이렇게 설계해야함
        //세터같은 거 쓰지 말자!

        findItem.setPrice(price); //(param.getPrice()); ->get말고 parameter 받아서 update하는 것이 더 나은 방법.
        findItem.setName(name); //(param.getName());
        findItem.setStockQuantity(stockQuantity); //(param.getStockQuantity());
        //변경감지로 데이터 변경하는 방법.
        //itemRepository.save(findItem); ->필요없음.
        //return findItem;
        //머지merge는 이걸 jpa가 해줌.
        //null로 반환될 우려가 있으니까 merge쓰지 말고 직접 지금 updateItem처럼 작성을 해야합니다. //최대한 merge 안 쓰겠다고 생각!
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }



}
