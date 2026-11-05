package com.paysense.api;

import com.paysense.api.SecurityModels.*;
import com.paysense.service.SecurityAssessmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/security")
public class SecurityController {
 private final SecurityAssessmentService assessment;
 public SecurityController(SecurityAssessmentService assessment){this.assessment=assessment;}
 @PostMapping("/analyze") public AnalyzeResponse analyze(@Valid @RequestBody AnalyzeRequest request){return assessment.analyze(request);}
 @GetMapping("/layers") public List<String> layers(){return assessment.layers();}
 @GetMapping("/scam-education/{type}") public ScamEducation education(@PathVariable String type){return assessment.education(type);}
}
