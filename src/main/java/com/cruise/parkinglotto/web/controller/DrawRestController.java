package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.domain.*;
import com.cruise.parkinglotto.domain.enums.ApprovalStatus;
import com.cruise.parkinglotto.global.jwt.JwtUtils;
import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.applicantService.ApplicantService;
import com.cruise.parkinglotto.service.drawService.DrawService;
import com.cruise.parkinglotto.service.memberService.MemberService;
import com.cruise.parkinglotto.service.parkingSpaceService.ParkingSpaceService;
import com.cruise.parkinglotto.service.priorityApplicantService.PriorityApplicantService;
import com.cruise.parkinglotto.web.converter.ApplicantConverter;
import com.cruise.parkinglotto.web.converter.DrawConverter;
import com.cruise.parkinglotto.web.converter.ParkingSpaceConverter;
import com.cruise.parkinglotto.web.converter.PriorityApplicantConverter;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantRequestDTO;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawRequestDTO;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceRequestDTO;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO;
import com.cruise.parkinglotto.web.dto.priorityApplicantDTO.PriorityApplicantResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.io.IOException;

@RestController
@RequestMapping("/api/draws")
@RequiredArgsConstructor
public class DrawRestController {
    private final DrawService drawService;
    private final ParkingSpaceService parkingSpaceService;
    private final JwtUtils jwtUtils;
    private final ApplicantService applicantService;
    private final PriorityApplicantService priorityApplicantService;


    //추첨 실행 후 결과 저장하는 API
    @Operation(summary = "추첨 실행 API", description = "path variable로 추첨 ID를 넘겨주면 해당 추첨을 실행합니다.")
    @PostMapping("/{drawId}/execution")
    public ApiResponse<Void> executeDraw(@PathVariable("drawId") Long drawId) throws IOException {
        drawService.executeDraw(drawId);

        return ApiResponse.onSuccess(SuccessStatus.DRAW_EXECUTE_RESULT, null);
    }

    //회차의 추첨 결과의 명단을 조회하는 API
    @Operation(summary = "해당 회차 추첨 결과 조회 API", description = "path variable로 추첨 ID를 넘겨주면 해당 추첨의 결과를 반환합니다.")
    @GetMapping("/{drawId}/result")
    public ApiResponse<DrawResponseDTO.DrawResultResponseDTO> getDrawResult(HttpServletRequest httpServletRequest, @PathVariable("drawId") Long drawId, @RequestParam("page") Integer page) {

        DrawResponseDTO.DrawResultResponseDTO drawResultResponseDTO = drawService.getDrawResult(httpServletRequest, drawId, page);
        return ApiResponse.onSuccess(SuccessStatus.DRAW_INFO_FOUND, drawResultResponseDTO);
    }

    @Operation(summary = "추첨 정보 상세 조회 API", description = "path variable로 상세조회할 추첨의 drawId를 전송해주세요.")
    @GetMapping("/{drawId}")
    public ApiResponse<DrawResponseDTO.GetCurrentDrawInfoDTO> getCurrentDrawInfo(HttpServletRequest httpServletRequest,
                                                                                 @PathVariable Long drawId) {

        DrawResponseDTO.GetCurrentDrawInfoDTO getCurrentDrawInfoDto = drawService.getCurrentDrawInfo(httpServletRequest, drawId);

        return ApiResponse.onSuccess(SuccessStatus.DRAW_INFO_FOUND, getCurrentDrawInfoDto);
    }

    @Operation(summary = "추첨 생성 임시저장 API", description = "화면상 추첨 정보 입력 후 \"다음\" 버튼을 클릭할 때 호출하는 API입니다. 추첨을 생성하기 위해 추첨기간, 사용기간, 세부설명을 입력하고 지도 이미지를 첨부합니다.  drawType을 \"GENERAL\"로 입력시 일반 추첨, \"PRIORITY\"로 입력시 우대 신청입니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<DrawResponseDTO.CreateDrawResultDTO> createDraw(@RequestPart(value = "mapImage", required = true) MultipartFile mapImage,
                                                                       @Valid @RequestPart(value = "createDrawRequestDTO", required = true) DrawRequestDTO.CreateDrawRequestDTO createDrawRequestDTO) {
        Draw draw = drawService.createDraw(mapImage, createDrawRequestDTO);
        return ApiResponse.onSuccess(SuccessStatus.DRAW_INFO_SAVED, DrawConverter.toCreateDrawResultDTO(draw));
    }

    @Operation(summary = "추첨 생성 완료 API", description = "path variable로 추첨 생성 임시저장 API에서 응답으로 받은 drawId를 입력해주세요. 추첨 정보와 해당 추첨의 주차 구역 목록을 확인하고 추첨 생성을 완료하는 API 입니다.")
    @PatchMapping("/{drawId}")
    public ApiResponse<DrawResponseDTO.ConfirmDrawCreationResultDTO> confirmDrawCreation(@PathVariable Long drawId) {
        DrawResponseDTO.ConfirmDrawCreationResultDTO drawCreationResultDTO = drawService.confirmDrawCreation(drawId);
        return ApiResponse.onSuccess(SuccessStatus.DRAW_CREATION_CONFIRMED, drawCreationResultDTO);
    }

    @Operation(summary = "페이징을 포함한 추첨 시뮬레이션 API", description = "path variable로 drawId, RequestParam으로 seedNum과 페이지를 넘겨주면 추첨 시뮬레이션을 실행합니다.")
    @GetMapping("/{drawId}/simulation")
    public ApiResponse<DrawResponseDTO.SimulateDrawResponseDTO> simulateDraw(@PathVariable("drawId") Long drawId, @RequestParam("seedNum") String seedNum, @RequestParam("page") Integer page) {
        DrawResponseDTO.SimulateDrawResponseDTO simulateDrawResponseDTO = drawService.simulateDraw(drawId, seedNum, page);
        return ApiResponse.onSuccess(SuccessStatus.DRAW_SIMULATE_COMPLETED, simulateDrawResponseDTO);
    }

    @Operation(summary = "(메인페이지용) 추첨정보, 추첨 경쟁률, 주차구역 별 경쟁률, 로그인한 사용자의 신청 여부를 반환하는 API입니다.", description = "진행 중인 추첨이 일반 신청일 경우 [추첨정보, 경쟁률, 주차구역 별 경쟁률]을 반환하고, 우대신청일 경우 [추첨정보]를 반환합니다.")
    @GetMapping("/overview")
    public ApiResponse<DrawResponseDTO.GetDrawOverviewResultDTO> getDrawInfo(HttpServletRequest httpServletRequest) {
        DrawResponseDTO.GetDrawOverviewResultDTO getDrawInfoResultDTO = drawService.getDrawOverview(httpServletRequest);
        return ApiResponse.onSuccess(SuccessStatus.DRAW_OVERVIEW_FOUND, getDrawInfoResultDTO);
    }

    @Operation(summary = "임시저장된 추첨에 주차구역을 추가하는 API입니다.", description = "path variable로 추첨의 drawId를 전송해주세요. MultipartFile 타입으로 주차구역 평면도 이미지를 전송헤주세요. addParkingSpaceDTO로 주차구역에 대한 정보를 입력헤주세요. ")
    @PostMapping(value = "/{drawId}/parking-spaces", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ParkingSpaceResponseDTO.AddParkingSpaceResultDTO> addParkingSpace(@PathVariable Long drawId,
                                                                                         @RequestPart(value = "floorPlanImage", required = true) MultipartFile floorPlanImage,
                                                                                         @RequestPart(value = "addParkingSpaceDTO", required = true) ParkingSpaceRequestDTO.AddParkingSpaceDTO addParkingSpaceDTO) {
        ParkingSpace parkingSpace = parkingSpaceService.addParkingSpace(drawId, floorPlanImage, addParkingSpaceDTO);
        return ApiResponse.onSuccess(SuccessStatus.PARKING_SPACE_ADDED, ParkingSpaceConverter.toAddParkingSpaceResultDTO(parkingSpace));
    }


    @Operation(summary = "일반 추첨 신청자 목록을 조회하는 API 입니다. 페이징을 포함합니다.", description = "Path variable로 신청자 목 drawId를 전송해주세요.  RequestParam 으로 page 번호를 전송해주세요.")
    @GetMapping("/{drawId}/applicants")
    public ApiResponse<ApplicantResponseDTO.GetApplicantListResultDTO> getApplicantList(@PathVariable(name = "drawId") Long drawId,
                                                                                        @RequestParam(name = "page") Integer page) {
        Page<Applicant> applicantList = applicantService.getApplicantList(page - 1, drawId);
        return ApiResponse.onSuccess(SuccessStatus.APPLICANT_LIST_FOUND, ApplicantConverter.toGetApplicantListResultDTO(applicantList));
    }

    @Operation(summary = "사용자가 일반 추첨을(GENERAL) 신청하는 api입니다.", description = "파일 리스트와 적절한 DTO를 인터페이스 명세서를 참조해서 넣어주세요.")
    @PostMapping(value = "/{drawId}/general/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> drawApply(HttpServletRequest httpServletRequest, @RequestPart(value = "certificateDocs", required = false) @Parameter(description = "업로드할 인증서 문서 리스트") List<MultipartFile> certificateDocs,
                                    @RequestPart(value = "applyDrawRequestDTO", required = true) @Parameter(description = "일반 추첨 신청에 필요한 요청 데이터") @Valid ApplicantRequestDTO.GeneralApplyDrawRequestDTO applyDrawRequestDTO) {
        String accountId = jwtUtils.getAccountIdFromRequest(httpServletRequest);
        applicantService.drawApply(certificateDocs, applyDrawRequestDTO, accountId);
        return ApiResponse.onSuccess(SuccessStatus.APPLICANT_SUCCESS, null);
    }

    @Operation(summary = "우대 신청을 승인하는 API 입니다.", description = " PathVariable 으로 drawId와 priorityApplicantId 번호를 전송해주세요.")
    @PatchMapping("/{drawId}/priority-applicants/{priorityApplicantId}/approval")
    public ApiResponse<PriorityApplicantResponseDTO.ApprovePriorityResultDTO> approvePriority(@PathVariable(name = "drawId") Long drawId,
                                                                                              @PathVariable(name = "priorityApplicantId") Long priorityApplicantId) {
        PriorityApplicantResponseDTO.ApprovePriorityResultDTO approvePriorityResultDTO = priorityApplicantService.approvePriority(drawId, priorityApplicantId);
        return ApiResponse.onSuccess(SuccessStatus.PRIORITY_APPLICANT_APPROVED, approvePriorityResultDTO);
    }

    @Operation(summary = "우대 신청 신청자 목록을 조회하는 API 입니다. 페이징을 포함합니다.", description = "RequestParam으로 approvalStatus(PENDING, APPROVED, REJECTED)와 page 번호를 전송해주세요.  PathVariable 으로 drawId를 전송해주세요.")
    @GetMapping("/{drawId}/priority-applicants")
    public ApiResponse<PriorityApplicantResponseDTO.GetPriorityApplicantListResultDTO> getApplicantList(@RequestParam(name = "approvalStatus") ApprovalStatus approvalStatus,
                                                                                                        @PathVariable(name = "drawId") Long drawId,
                                                                                                        @RequestParam(name = "page") Integer page) {
        Page<PriorityApplicant> priorityApplicantList = priorityApplicantService.getPriorityApplicantList(page - 1, drawId, approvalStatus);
        return ApiResponse.onSuccess(SuccessStatus.PRIORITY_APPLICANT_LIST_FOUND, PriorityApplicantConverter.toGetPriorityApplicantListResultDTO(priorityApplicantList));
    }

    @Operation(summary = "일반 추첨 신청자 목록에서 검색하는 API 입니다.", description = "검색 키워드로 사원명 또는 사번을 입력해주세요.")
    @GetMapping("/list/search")
    public ApiResponse<ApplicantResponseDTO.GetApplicantResultDTO> searchApplicant(@RequestParam(name = "searchKeyword") String searchKeyword) {

        return ApiResponse.onSuccess(SuccessStatus.APPLICANT_SEARCH_FOUND, applicantService.searchApplicantBySearchKeyword(searchKeyword));
    }

    @Operation(summary = "추첨 결과 엑셀 저장 API", description = "추첨의 최초 결과를 엑셀로 저장할 수 있는 API 입니다. drawId를 넘겨주면 해당 추첨의 결과를 엑셀로 저장합니다.")
    @GetMapping("/{drawId}/result-excel")
    public ApiResponse<DrawResponseDTO.DrawResultExcelDTO> downloadResultExcel(@PathVariable("drawId") Long drawId) {
        DrawResponseDTO.DrawResultExcelDTO drawResultExcelDTO = drawService.getDrawResultExcel(drawId);
        return ApiResponse.onSuccess(SuccessStatus.DRAW_RESULT_EXCEL_DOWNLOADED, drawResultExcelDTO);

    }
}