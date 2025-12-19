package com.pe.laboratorio.subexam.service;

import java.util.List;

import com.pe.laboratorio.subexam.dto.SubExamRequest;
import com.pe.laboratorio.subexam.dto.SubExamResponse;

public interface SubExamService {

    SubExamResponse create(SubExamRequest request);

    SubExamResponse update(Long id, SubExamRequest request);

    SubExamResponse getById(Long id);

    List<SubExamResponse> getByExamenId(Long examenId);

    void delete(Long id);

    void toggleStatus(Long id, boolean active);

    void reorderSubExams(Long examenId, List<Long> subExamIds);
}