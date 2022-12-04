package com.sparta.springcore2.controller;


import com.sparta.springcore2.dto.FolderRequestDto;
import com.sparta.springcore2.exception.RestApiException;
import com.sparta.springcore2.model.Folder;
import com.sparta.springcore2.model.Product;
import com.sparta.springcore2.model.User;
import com.sparta.springcore2.security.UserDetailsImpl;
import com.sparta.springcore2.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FolderController {

    private final FolderService folderService;

    @Autowired
    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping("api/folders")
    public List<Folder> addFolders(
            @RequestBody FolderRequestDto folderRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) {
        List<String> folderNames = folderRequestDto.getFolderNames();
        User user = userDetails.getUser();

        return folderService.addFolders(folderNames, user);
    }

    @GetMapping("api/folders")
    public List<Folder> getFolders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return folderService.getFolders(userDetails.getUser());
    }

    @GetMapping("api/folders/{folderId}/products")
    public Page<Product> getProductsInFolder(
            @PathVariable Long folderId,
            @RequestParam() int page,
            @RequestParam() int size,
            @RequestParam() String sortBy,
            @RequestParam() boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        page = page - 1;
        return folderService.getProductsInFolder(folderId, page, size, sortBy, isAsc, userDetails.getUser());
    }
}
