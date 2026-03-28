package com.dayun.mlkitfacecount

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 얼굴 검출 ViewModel.
 *
 * AndroidViewModel을 사용하는 이유:
 * - InputImage.fromFilePath()에 Context가 필요
 * - Application context는 Activity 생명주기와 무관하게 안전
 */
class FaceDetectionViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(FaceDetectionUiState())
    val uiState: StateFlow<FaceDetectionUiState> = _uiState.asStateFlow()

    /**
     * ML Kit Face Detector 설정.
     *
     * PERFORMANCE_MODE_ACCURATE vs PERFORMANCE_MODE_FAST:
     * - ACCURATE: 정확도 우선. 작은 얼굴, 측면 얼굴도 잘 검출. 정적 이미지 분석에 적합.
     * - FAST: 속도 우선. 실시간 카메라 프리뷰 등에서 사용. 정확도는 다소 떨어짐.
     *
     * 이 앱은 정적 이미지만 처리하므로 ACCURATE 모드 사용.
     */
    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setMinFaceSize(0.1f) // 이미지 너비 대비 최소 얼굴 크기 비율
            .build()
    )

    /**
     * 사용자가 이미지를 선택했을 때 호출.
     * 기존 결과를 초기화하고 새 이미지에 대해 얼굴 검출 시작.
     */
    fun onImageSelected(uri: Uri) {
        _uiState.update {
            it.copy(
                imageUri = uri,
                faceCount = 0,
                faceRects = emptyList(),
                isLoading = true,
                errorMessage = null,
                imageWidth = 0,
                imageHeight = 0,
            )
        }
        detectFaces(uri)
    }

    private fun detectFaces(uri: Uri) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()

                // IO 스레드에서 이미지 로드 (디스크 I/O 발생)
                val inputImage = withContext(Dispatchers.IO) {
                    InputImage.fromFilePath(context, uri)
                }

                // InputImage의 width/height는 EXIF 회전이 반영된 실제 크기
                val imageWidth = inputImage.width
                val imageHeight = inputImage.height

                // ML Kit 얼굴 검출 실행 (비동기 Task 기반)
                detector.process(inputImage)
                    .addOnSuccessListener { faces ->
                        _uiState.update {
                            it.copy(
                                faceCount = faces.size,
                                faceRects = faces.map { face -> face.boundingBox },
                                isLoading = false,
                                imageWidth = imageWidth,
                                imageHeight = imageHeight,
                            )
                        }
                    }
                    .addOnFailureListener { e ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "얼굴 검출 실패: ${e.localizedMessage}",
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "이미지 로드 실패: ${e.localizedMessage}",
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        detector.close()
    }
}
