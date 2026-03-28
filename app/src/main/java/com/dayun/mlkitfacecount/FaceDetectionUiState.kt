package com.dayun.mlkitfacecount

import android.graphics.Rect
import android.net.Uri

/**
 * 얼굴 검출 화면의 UI 상태.
 *
 * @property imageUri 사용자가 선택한 이미지의 content:// URI
 * @property faceCount 검출된 얼굴 수
 * @property faceRects ML Kit이 반환한 얼굴별 bounding box (원본 이미지 좌표계)
 * @property isLoading 검출 진행 중 여부
 * @property errorMessage 실패 시 에러 메시지
 * @property imageWidth ML Kit이 분석한 이미지의 너비 (EXIF 회전 반영)
 * @property imageHeight ML Kit이 분석한 이미지의 높이 (EXIF 회전 반영)
 */
data class FaceDetectionUiState(
    val imageUri: Uri? = null,
    val faceCount: Int = 0,
    val faceRects: List<Rect> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val imageWidth: Int = 0,
    val imageHeight: Int = 0,
)
