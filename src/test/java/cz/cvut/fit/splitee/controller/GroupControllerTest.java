package cz.cvut.fit.splitee.controller;

import cz.cvut.fit.splitee.entity.Group;
import cz.cvut.fit.splitee.service.GroupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // we mock the http request and we don't need a server
public class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    @Test
    public void getGroup() throws Exception {
        Group group = new Group("", "GroupTest", "CZK", null, null);

        String responseBody = "{\"code\":\"\",\"name\":\"GroupTest\",\"currency\":\"CZK\",\"photo\":null,\"description\":null}";

        when(groupService.findById(anyInt())).thenReturn(Optional.of(group));

        this.mockMvc
                .perform(get("/api/groups/id/1"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().string(responseBody));

        when(groupService.findByCode(anyString())).thenReturn(Optional.of(group));

        this.mockMvc
                .perform(get("/api/groups/code/1"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().string(responseBody));
    }

    @Test
    public void updateGroup() throws Exception{
        Group group = new Group("", "GroupTest", "CZK", null, null);

        when(groupService.findById(anyInt())).thenReturn(Optional.of(group));

    }

    @Test
    public void listMembers(){
        //when(groupService.findAllMembersById().thenReturn(Optional.of(group));
    }

}