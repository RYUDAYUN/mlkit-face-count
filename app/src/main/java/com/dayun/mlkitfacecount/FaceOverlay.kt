package com.dayun.mlkitfacecount

import android.graphics.Rect
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dayun.mlkitfacecount.ui.theme.BoundingBoxFill
import com.dayun.mlkitfacecount.ui.theme.BoundingBoxLabelBg
import com.dayun.mlkitfacecount.ui.theme.BoundingBoxLabelText
import com.dayun.mlkitfacecount.ui.theme.BoundingBoxStroke

// ── Drawing constants ─────────────────────────────────────────────────────────
private val StrokeWidth = 3.dp
private val CornerRadiusDp = 8.dp
private val LabelPaddingH = 6.dp
private val LabelPaddingV = 3.dp
private val LabelFontSize = 11.sp

/**
 * 이미지 위에 얼굴 bounding box를 오버레이하는 Composable.
 *
 * 좌표계 변환:
 *   scale = min(displayW / imageW, displayH / imageH)   ← ContentScale.Fit 기준
 *   offset = 중앙 정렬 여백
 *   화면좌표 = 원본좌표 × scale + offset
 *
 * 시각적 처리:
 *   - 반투명 틸 필 (20 % 불투명) + 라운드 코너 스트로크
 *   - 각 얼굴 번호(#1, #2 …)를 좌상단 chip에 표시
 *
 * @param imageUri 표시할 이미지 URI
 * @param faceRects ML Kit이 반환한 bounding box 목록 (원본 이미지 좌표계)
 * @param imageWidth 원본 이미지 너비 (EXIF 회전 반영)
 * @param imageHeight 원본 이미지 높이 (EXIF 회전 반영)
 */
@Composable
fun ImageWithFaceOverlay(
    imageUri: Uri,
    faceRects: List<Rect>,
    imageWidth: Int,
    imageHeight: Int,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = "선택한 이미지",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
        )

        if (faceRects.isNotEmpty() && imageWidth > 0 && imageHeight > 0) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val scaleX = size.width / imageWidth.toFloat()
                val scaleY = size.height / imageHeight.toFloat()
                val scale = minOf(scaleX, scaleY)

                val offsetX = (size.width - imageWidth * scale) / 2f
                val offsetY = (size.height - imageHeight * scale) / 2f

                faceRects.forEachIndexed { index, rect ->
                    drawFaceBoundingBox(
                        rect = rect,
                        scale = scale,
                        offsetX = offsetX,
                        offsetY = offsetY,
                        label = "#${index + 1}",
                        textMeasurer = textMeasurer,
                    )
                }
            }
        }
    }
}

// ── Private drawing helper ────────────────────────────────────────────────────

private fun DrawScope.drawFaceBoundingBox(
    rect: Rect,
    scale: Float,
    offsetX: Float,
    offsetY: Float,
    label: String,
    textMeasurer: TextMeasurer,
) {
    val left = rect.left * scale + offsetX
    val top = rect.top * scale + offsetY
    val right = rect.right * scale + offsetX
    val bottom = rect.bottom * scale + offsetY
    val w = right - left
    val h = bottom - top

    val cornerRadius = CornerRadius(CornerRadiusDp.toPx())

    // Semi-transparent fill
    drawRoundRect(
        color = BoundingBoxFill,
        topLeft = Offset(left, top),
        size = Size(w, h),
        cornerRadius = cornerRadius,
    )

    // Stroke outline
    drawRoundRect(
        color = BoundingBoxStroke,
        topLeft = Offset(left, top),
        size = Size(w, h),
        cornerRadius = cornerRadius,
        style = Stroke(width = StrokeWidth.toPx()),
    )

    // Label chip (top-left corner of the box)
    val labelStyle = TextStyle(
        fontSize = LabelFontSize,
        fontWeight = FontWeight.Bold,
        color = BoundingBoxLabelText,
    )
    val measured = textMeasurer.measure(label, labelStyle)
    val chipPadH = LabelPaddingH.toPx()
    val chipPadV = LabelPaddingV.toPx()
    val chipW = measured.size.width + chipPadH * 2
    val chipH = measured.size.height + chipPadV * 2

    // Chip background — anchored at the top-left of the bounding box
    drawRoundRect(
        color = BoundingBoxLabelBg,
        topLeft = Offset(left, top),
        size = Size(chipW, chipH),
        cornerRadius = CornerRadius(4.dp.toPx()),
    )

    // Chip label text
    drawText(
        textMeasurer = textMeasurer,
        text = label,
        style = labelStyle,
        topLeft = Offset(left + chipPadH, top + chipPadV),
    )
}
