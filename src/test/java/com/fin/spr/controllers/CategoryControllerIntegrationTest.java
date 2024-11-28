package com.fin.spr.controllers;

import com.fin.spr.BaseIntegrationTest;
import com.fin.spr.exceptions.EntityAlreadyExistsException;
import com.fin.spr.models.Category;
import com.fin.spr.services.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private Category testCategory1;
    private Category testCategory2;

    private static final String BASE_URL = "/api/v1/places/categories";

    @BeforeEach
    public void setup() {
        testCategory1 = new Category(1, "museums", "Museums");
        testCategory2 = new Category(2, "parks", "Parks");
    }

    @Test
    public void testGetAllCategories() throws Exception {
        Mockito.when(categoryService.getAllCategories()).thenReturn(Arrays.asList(testCategory1, testCategory2));
        mockMvc.perform(get(BASE_URL)
                .header("Authorization", userBearerToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Museums"))
                .andExpect(jsonPath("$[1].name").value("Parks"));
    }

    @Test
    public void testGetCategoryById_Success() throws Exception {
        Mockito.when(categoryService.getCategoryById(1)).thenReturn(Optional.of(testCategory1));

        mockMvc.perform(get(BASE_URL + "/1")
                .header("Authorization", userBearerToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Museums"))
                .andExpect(jsonPath("$.slug").value("museums"));
    }

    @Test
    public void testGetCategoryById_NotFound() throws Exception {
        Mockito.when(categoryService.getCategoryById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + "/99")
                .header("Authorization", userBearerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateCategory_Success() throws Exception {
        Mockito.doNothing().when(categoryService).createCategory(any(Category.class));

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", adminBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCategory1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Museums"))
                .andExpect(jsonPath("$.slug").value("museums"));
    }

    @Test
    public void testCreateCategory_Conflict() throws Exception {
        Mockito.doThrow(new EntityAlreadyExistsException("Category already exists")).when(categoryService).createCategory(any(Category.class));

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", adminBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCategory1)))
                .andExpect(status().isConflict());
    }

    @Test
    public void testUpdateCategory_Success() throws Exception {
        Mockito.when(categoryService.updateCategory(eq(1), any(Category.class))).thenReturn(true);

        mockMvc.perform(put(BASE_URL + "/1")
                        .header("Authorization", adminBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCategory1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Museums"))
                .andExpect(jsonPath("$.slug").value("museums"));
    }

    @Test
    public void testUpdateCategory_NotFound() throws Exception {
        Mockito.when(categoryService.updateCategory(eq(99), any(Category.class))).thenReturn(false);

        mockMvc.perform(put(BASE_URL + "/99")
                        .header("Authorization", adminBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCategory1)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteCategory_Success() throws Exception {
        Mockito.when(categoryService.deleteCategory(1)).thenReturn(true);

        mockMvc.perform(delete(BASE_URL + "/1")
                        .header("Authorization", adminBearerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteCategory_NotFound() throws Exception {
        Mockito.when(categoryService.deleteCategory(99)).thenReturn(false);

        mockMvc.perform(delete(BASE_URL + "/99")
                        .header("Authorization", adminBearerToken))
                .andExpect(status().isNotFound());
    }
}

// ls7