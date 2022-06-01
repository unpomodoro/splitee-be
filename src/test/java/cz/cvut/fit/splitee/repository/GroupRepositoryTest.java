package cz.cvut.fit.splitee.repository;

import cz.cvut.fit.splitee.entity.Group;
import cz.cvut.fit.splitee.entity.Membership;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GroupRepositoryTest {

    @Autowired
    private GroupRepository groupRepository;

    @BeforeAll
    public void create() {
        Group group = new Group("AAAAAAA","GroupTest", "CZK", null, null);

        Set<Membership> members = new HashSet<>() {
        };
        members.add(new Membership("member1", null, "123456789/2300"));
        members.add(new Membership("member2", null, "223456789/2300"));
        members.add(new Membership("member3", null, "323456789/2300"));
        members.add(new Membership("member4", null, "423456789/2300"));

        group.setMemberships(members);

        groupRepository.save(group);
    }

    @Test
    public void findGroup () {
        Group group = new Group("XXXXXXX", "GroupTest", "CZK", null, null);
        Optional<Group> optional = groupRepository.findByCode("XXXXXXX");
        assertEquals(true, optional.get().equals(group));
    }

    @Test
    public void findMembers(){
        Optional<Group> g = groupRepository.findByCode("AAAAAAA");

        Collection<Membership> members = groupRepository.findAllMembersById(g.get().getId());
        assertNotNull(members);
        assertEquals(4, members.size());
    }
}

