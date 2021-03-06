package me.wired.learning.user;

import me.wired.learning.common.BaseControllerTest;
import me.wired.learning.common.TestDescription;
import org.junit.Test;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class XUserControllerTest extends BaseControllerTest {

    @Test
    @TestDescription("User 등록(정상)")
    public void createNormalUser() throws Exception {
        int index = 1932;
        String userId = index + preUsers.getUserVariableId();
        String userPassword = preUsers.getUserPassword();

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(XUserGenerator.newNormalXUserDto(index, userId, userPassword))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("create-user",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("The MIME type of this content"),
                                headerWithName(HttpHeaders.ACCEPT).description("Media type(s) that is(/are) acceptable for the response")
                        ),
                        requestFields(
                                fieldWithPath("variableId").description("User name"),
                                fieldWithPath("password").description("User description"),
                                fieldWithPath("name").description("User startEnrollmentDateTime"),
                                fieldWithPath("roles").description("User endEnrollmentDateTime")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type header"),
                                headerWithName(HttpHeaders.LOCATION).description("Location header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("User ID"),
                                fieldWithPath("variableId").description("User name"),
                                //fieldWithPath("password").description("User description"),
                                fieldWithPath("name").description("User startEnrollmentDateTime"),
                                fieldWithPath("roles").description("User endEnrollmentDateTime"),
                                fieldWithPath("_links.self.href").description("Link to self"),
                                fieldWithPath("_links.update-user.href").description("Link to update"),
                                fieldWithPath("_links.delete-user.href").description("Link to delete"),
                                fieldWithPath("_links.profile.href").description("Link to profile")
                        ),
                        links(
                                linkWithRel("self").description("Link to self"),
                                linkWithRel("update-user").description("Link to update"),
                                linkWithRel("delete-user").description("Link to delete"),
                                linkWithRel("profile").description("Link to profile")
                        )
                ));
    }

    @Test
    @TestDescription("User 등록(비정상: subId 포맷 에러)")
    public void createWrongUser1() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(XUserGenerator.newWrongXUserDto1())))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("User 등록(비정상: password 길이 에러)")
    public void createWrongUser2() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(XUserGenerator.newWrongXUserDto2())))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("User 등록(비정상: 이미 등록된 subId)")
    public void createWrongUser3() throws Exception {
        int index = 2144;
        String userId = index + preUsers.getUserVariableId();
        String userPassword = preUsers.getUserPassword();

        ResultActions resultActions = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(XUserGenerator.newNormalXUserDto(index, userId, userPassword))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(XUserGenerator.newNormalXUserDto(index, userId, userPassword))))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("User 정보 읽기(정상)")
    public void readUser() throws Exception {
        int index = 543;
        String userId = index + preUsers.getUserVariableId();
        String userPassword = preUsers.getUserPassword();

        ResultActions resultActions = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(XUserGenerator.newNormalXUserDto(index, userId, userPassword))))
                .andExpect(status().isCreated());

        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        String id = (String) jsonParser.parseMap(responseData).get("id");
        mockMvc.perform(get("/api/users/{id}", id)
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_TYPE))
                .andExpect(jsonPath("id").exists())
                .andDo(document("read-user",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("Media type(s) that is(/are) acceptable for the response")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("User ID"),
                                fieldWithPath("variableId").description("User name"),
                                //fieldWithPath("password").description("User description"),
                                fieldWithPath("name").description("User startEnrollmentDateTime"),
                                fieldWithPath("roles").description("User endEnrollmentDateTime"),
                                fieldWithPath("_links.self.href").description("Link to self"),
                                fieldWithPath("_links.update-user.href").description("Link to update"),
                                fieldWithPath("_links.delete-user.href").description("Link to delete"),
                                fieldWithPath("_links.profile.href").description("Link to profile")
                        ),
                        links(
                                linkWithRel("self").description("Link to self"),
                                linkWithRel("update-user").description("Link to update"),
                                linkWithRel("delete-user").description("Link to delete"),
                                linkWithRel("profile").description("Link to profile")
                        )
                ));
    }

    @Test
    @TestDescription("User 정보 읽기(비정상: 존재하지 않는 User)")
    public void readWrongUser() throws Exception {
        String id = "UserNotExists";
        mockMvc.perform(get("/api/users/{id}", id)
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @TestDescription("여러 개의 User 읽기 테스트(1 페이지, 10개, name/desc 정렬)")
    public void readUsers() throws Exception {
        IntStream.range(0, 30).forEach(i -> {
            int index = i * 11;
            String userId = index + preUsers.getUserVariableId();
            String userPassword = preUsers.getUserPassword();

            XUserDto userDto = XUserGenerator.newNormalXUserDto(index, userId, userPassword);
            try {
                ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                        .andExpect(status().isCreated());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        int page = 1;
        int size = 10;
        String sort = "name,ASC";
        mockMvc.perform(get("/api/users")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("sort", sort)
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("read-users",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("Media type(s) that is(/are) acceptable for the response")
                        ),
                        requestParameters(
                                parameterWithName("page").description("request page"),
                                parameterWithName("size").description("request size"),
                                parameterWithName("sort").description("request sort")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.xUserList[].id").description("User ID"),
                                fieldWithPath("_embedded.xUserList[].variableId").description("User name"),
                                //fieldWithPath("_embedded.xUserList[].password").description("User description"),
                                fieldWithPath("_embedded.xUserList[].name").description("User startEnrollmentDateTime"),
                                fieldWithPath("_embedded.xUserList[].roles").description("User endEnrollmentDateTime"),
                                fieldWithPath("_embedded.xUserList[]._links.self.href").description("Link to self"),
                                fieldWithPath("_embedded.xUserList[]._links.update-user.href").description("Link to update"),
                                fieldWithPath("_embedded.xUserList[]._links.delete-user.href").description("Link to delete"),
                                fieldWithPath("page.size").description("size per page"),
                                fieldWithPath("page.totalElements").description("Total elements"),
                                fieldWithPath("page.totalPages").description("Total pages"),
                                fieldWithPath("page.number").description("number of page"),
                                fieldWithPath("_links.first.href").description("Link to first page"),
                                fieldWithPath("_links.prev.href").description("Link to previous page"),
                                fieldWithPath("_links.self.href").description("Link to self"),
                                fieldWithPath("_links.next.href").description("Link to next page"),
                                fieldWithPath("_links.last.href").description("Link to last page"),
                                fieldWithPath("_links.create-user.href").description("Link to create course"),
                                fieldWithPath("_links.profile.href").description("Link to profile")
                        ),
                        links(
                                linkWithRel("first").description("Link to first page"),
                                linkWithRel("prev").description("Link to previous page"),
                                linkWithRel("self").description("Link to self"),
                                linkWithRel("next").description("Link to next page"),
                                linkWithRel("last").description("Link to last page"),
                                linkWithRel("create-user").description("Link to create course"),
                                linkWithRel("profile").description("Link to profile")
                        )
                ));
    }

    @Test
    @TestDescription("User가 한 개도 존재하지 않을 때, 여러 개의 User 읽기 테스트(1 페이지, 10개, name/desc 정렬)")
    public void readUsers2() throws Exception {
        int page = 1;
        int size = 10;
        String sort = "name,DESC";
        mockMvc.perform(get("/api/users")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("sort", sort)
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    @TestDescription("User 정보 업데이트하기")
    public void updateUser() throws Exception {
        int index = 9865;
        String userId = index + preUsers.getUserVariableId();
        String userPassword = preUsers.getUserPassword();

        XUserDto userDto = XUserGenerator.newNormalXUserDto(index, userId, userPassword);

        ResultActions resultActions = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated());

        final String newName = "New Name";
        final String newPassword = "NewPassword";

        userDto.setName(newName);
        userDto.setPassword(newPassword);

        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        String id = (String) jsonParser.parseMap(responseData).get("id");
        mockMvc.perform(put("/api/users/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, getUserBearerToken(String.valueOf(index)))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_TYPE))
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("name").value(newName))
                .andDo(document("update-user",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authentication credentials for HTTP authentication"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("The MIME type of this content"),
                                headerWithName(HttpHeaders.ACCEPT).description("Media type(s) that is(/are) acceptable for the response")
                        ),
                        requestFields(
                                fieldWithPath("variableId").description("User name"),
                                fieldWithPath("password").description("User description"),
                                fieldWithPath("name").description("User startEnrollmentDateTime"),
                                fieldWithPath("roles").description("User endEnrollmentDateTime")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("User ID"),
                                fieldWithPath("variableId").description("User name"),
                                //fieldWithPath("password").description("User description"),
                                fieldWithPath("name").description("User startEnrollmentDateTime"),
                                fieldWithPath("roles").description("User endEnrollmentDateTime"),
                                fieldWithPath("_links.self.href").description("Link to self"),
                                fieldWithPath("_links.update-user.href").description("Link to update"),
                                fieldWithPath("_links.delete-user.href").description("Link to delete"),
                                fieldWithPath("_links.profile.href").description("Link to profile")
                        ),
                        links(
                                linkWithRel("self").description("Link to self"),
                                linkWithRel("update-user").description("Link to update"),
                                linkWithRel("delete-user").description("Link to delete"),
                                linkWithRel("profile").description("Link to profile")
                        )
                ));
    }

    @Test
    @TestDescription("User 정보 삭제")
    public void deleteUser() throws Exception {
        int index = 173493;
        String userId = index + preUsers.getUserVariableId();
        String userPassword = preUsers.getUserPassword();

        ResultActions resultActions = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(XUserGenerator.newNormalXUserDto(index, userId, userPassword))))
                .andExpect(status().isCreated());

        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        String id = (String) jsonParser.parseMap(responseData).get("id");
        mockMvc.perform(delete("/api/users/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, getUserBearerToken(String.valueOf(index))))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("delete-user",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authentication credentials for HTTP authentication")
                        )
                ))
        ;
    }

    @Test
    @TestDescription("존재하지 않는 User 정보 삭제")
    public void deleteWrongUser() throws Exception {
        String id = "1231231231232113132";
        mockMvc.perform(delete("/api/users/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, getUserBearerToken(null)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }
}
