package cz.cvut.fit.splitee.entity;

import cz.cvut.fit.splitee.repository.GroupRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GroupTest {

    @Autowired
    private GroupRepository groupRepository;

    @BeforeAll
    public void create() {
        Group group = new Group("XXXXXXX", "GroupTest", "CZK", null, null);
        groupRepository.save(group);
    }

    @Test
    public void findGroup () {
        Group group = new Group("XXXXXXX", "GroupTest", "CZK", null, null);
        Optional<Group> optional = groupRepository.findByCode("XXXXXXX");
        assertEquals(true, optional.get().equals(group));
    }
}
