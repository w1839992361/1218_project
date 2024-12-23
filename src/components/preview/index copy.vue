<template>
    <div style="width: 100%; height: 400px;">
        <!-- 如果没有 fileUrl，显示文件上传控件 -->
        <input v-if="!fileUrl" type="file" @change="handleFileUpload" />

        <!-- 如果有文件内容或文件URL，显示文件内容 -->
        <div v-if="fileContent || fileUrl">
            <h3>文件内容预览</h3>

            <!-- 显示文本文件 -->
            <div v-if="fileType === 'txt'">{{ fileContent }}</div>

            <!-- 显示 HTML 文件 -->
            <div v-if="fileType === 'html'" v-html="fileContent"></div>

            <!-- 显示 PDF 文件 -->
            <div v-if="fileType === 'pdf'">
                <canvas ref="pdfCanvas"></canvas>
                <div class="pdf-controls">
                    <button @click="changePage(-1)" :disabled="currentPage <= 1">上一页</button>
                    <span>{{ currentPage }} / {{ totalPages }}</span>
                    <button @click="changePage(1)" :disabled="currentPage >= totalPages">下一页</button>
                    <br />
                    <button @click="zoomIn">放大</button>
                    <button @click="zoomOut">缩小</button>
                </div>
            </div>

            <!-- 显示视频文件 -->
            <div v-if="fileType === 'mp4'">
                <video controls :src="fileContent"></video>
            </div>

            <!-- 显示 Excel 文件 -->
            <div v-if="fileType === 'xls'">
                <table>
                    <thead>
                        <tr>
                            <th v-for="(header, index) in sheetHeaders" :key="index">{{ header }}</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(row, index) in sheetData" :key="index">
                            <td v-for="(cell, colIndex) in row" :key="colIndex">{{ cell }}</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- 显示 Word 文件 -->
            <div v-if="fileType === 'doc'">
                <div v-html="fileContent"></div>
            </div>

            <!-- 显示 PowerPoint 文件 -->
            <div v-if="fileType === 'ppt'">
                <div v-html="fileContent"></div>
            </div>
        </div>
    </div>
</template>

<script>
import * as XLSX from 'xlsx';
import mammoth from 'mammoth';
import * as pdfjsLib from 'pdfjs-dist';

export default {
    props: {
        fileUrl: {
            type: String,
            default: null
        }
    },
    data() {
        return {
            fileContent: null,
            fileType: null,
            sheetData: [],
            sheetHeaders: [],
            pdfDocument: null,
            currentPage: 1,
            totalPages: 0,
            scale: 1.0 // 默认缩放比例为 1
        };
    },
    watch: {
        // 监听 fileUrl 属性的变化，自动加载文件
        fileUrl: 'loadFile'
    },
    created() {
        pdfjsLib.GlobalWorkerOptions.workerSrc = require('/pdfjs-dist/build/pdf.worker.entry'); // PDF.js Worker path
        if (this.fileUrl) {
            this.loadFile(this.fileUrl);
        }
    },
    methods: {
        // 文件上传处理
        async handleFileUpload(event) {
            const file = event.target.files[0];
            if (!file) return;

            const fileType = file.type.split('/')[0];
            this.fileType = fileType;

            if (fileType === 'text') {
                this.readTextFile(file);
            } else if (file.type === 'application/vnd.ms-excel' || file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet') {
                this.readExcelFile(file);
            } else if (file.type === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document') {
                this.readDocxFile(file);
            } else if (file.type === 'application/pdf') {
                this.readPdfFile(file);
            } else if (file.type === 'video/mp4') {
                this.previewMp4(file);
            } else if (file.type === 'application/vnd.ms-powerpoint') {
                this.readPptFile(file);
            } else if (file.type === 'text/html') {
                this.readHtmlFile(file);
            }
        },

        // 加载文件
        loadFile(fileUrl) {
            const fileType = fileUrl.split('.').pop().toLowerCase();
            this.fileType = fileType;
            if (fileType === 'txt') {
                this.fetchTextFile(fileUrl);
            } else if (fileType === 'html') {
                this.fetchHtmlFile(fileUrl);
            } else if (fileType === 'pdf') {
                this.fetchPdfFile(fileUrl);
            } else if (fileType === 'mp4') {
                this.previewMp4FromUrl(fileUrl);
            } else if (fileType === 'xls') {
                this.fetchExcelFile(fileUrl);
            } else if (fileType === 'doc' || fileType === 'ppt') {
                this.fetchDocPptFile(fileUrl);
            }
        },

        // 读取文本文件
        fetchTextFile(fileUrl) {
            fetch(fileUrl)
                .then(response => response.text())
                .then(data => {
                    this.fileContent = data;
                });
        },

        // 读取 HTML 文件
        fetchHtmlFile(fileUrl) {
            fetch(fileUrl)
                .then(response => response.text())
                .then(data => {
                    this.fileContent = data;
                });
        },

        // 读取 PDF 文件
        fetchPdfFile(fileUrl) {
            fetch(fileUrl)
                .then(response => response.arrayBuffer())
                .then(arrayBuffer => {
                    const loadingTask = pdfjsLib.getDocument(arrayBuffer);
                    loadingTask.promise
                        .then((pdf) => {
                            this.pdfDocument = pdf;
                            this.currentPage = 1;
                            this.totalPages = pdf.numPages;
                            console.log('PDF loaded successfully');
                            this.renderPage(this.currentPage); // 加载第一页
                        })
                        .catch(error => {
                            console.error('Error loading PDF document:', error);
                        });
                })
                .catch(error => {
                    console.error('Error fetching PDF file:', error);
                });
        },

        // 视频预览
        previewMp4FromUrl(fileUrl) {
            this.fileContent = fileUrl;
        },

        // 读取 Excel 文件
        fetchExcelFile(fileUrl) {
            fetch(fileUrl)
                .then(response => response.arrayBuffer())
                .then((arrayBuffer) => {
                    const workbook = XLSX.read(arrayBuffer, { type: 'array' });
                    const sheetName = workbook.SheetNames[0];
                    const sheet = workbook.Sheets[sheetName];
                    this.sheetData = XLSX.utils.sheet_to_json(sheet, { header: 1 });
                    this.sheetHeaders = this.sheetData[0];
                });
        },

        // 读取 Word 文件
        fetchDocPptFile(fileUrl) {
            fetch(fileUrl)
                .then(response => response.arrayBuffer())
                .then((arrayBuffer) => {
                    if (fileUrl.endsWith('.doc')) {
                        mammoth.extractRawText({ arrayBuffer })
                            .then((result) => {
                                this.fileContent = result.value;
                            });
                    } else {
                        // PowerPoint 文件示例处理
                        this.fileContent = 'PPT 内容未在此示例中解析。';
                    }
                });
        },

        // 处理文本文件
        readTextFile(file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                this.fileContent = e.target.result;
            };
            reader.readAsText(file);
        },

        // 处理 Excel 文件
        readExcelFile(file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                const data = e.target.result;
                const workbook = XLSX.read(data, { type: 'binary' });
                const sheetName = workbook.SheetNames[0];
                const sheet = workbook.Sheets[sheetName];
                this.sheetData = XLSX.utils.sheet_to_json(sheet, { header: 1 });
                this.sheetHeaders = this.sheetData[0];
            };
            reader.readAsBinaryString(file);
        },

        // 处理 Docx 文件
        readDocxFile(file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                const arrayBuffer = e.target.result;
                mammoth.extractRawText({ arrayBuffer })
                    .then((result) => {
                        this.fileContent = result.value;
                    });
            };
            reader.readAsArrayBuffer(file);
        },

        // 处理 PDF 文件
        readPdfFile(file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                const arrayBuffer = e.target.result;
                const loadingTask = pdfjsLib.getDocument(arrayBuffer);
                loadingTask.promise.then((pdf) => {
                    pdf.getPage(1).then((page) => {
                        const canvas = this.$refs.pdfCanvas;
                        const context = canvas.getContext('2d');
                        const viewport = page.getViewport({ scale: 1.0 });
                        canvas.height = viewport.height;
                        canvas.width = viewport.width;
                        page.render({
                            canvasContext: context,
                            viewport: viewport
                        });
                    });
                });
            };
            reader.readAsArrayBuffer(file);
        },

        // 视频预览
        previewMp4(file) {
            this.fileContent = URL.createObjectURL(file);
        },

        // 读取 HTML 文件
        readHtmlFile(file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                this.fileContent = e.target.result;
            };
            reader.readAsText(file);
        },

        // 处理 PowerPoint 文件
        readPptFile(file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                this.fileContent = e.target.result;
            };
            reader.readAsBinaryString(file);
        },

        // 渲染 PDF 页
        async renderPage(pageNum) {
            try {
                if (!this.pdfDocument) {
                    console.error('PDF document is not loaded.');
                    return;
                }

                const page = await this.pdfDocument.getPage(pageNum);

                const canvas = this.$refs.pdfCanvas;
                if (!canvas) {
                    console.error('Canvas element is not available.');
                    return;
                }

                const context = canvas.getContext('2d');
                const viewport = page.getViewport({ scale: this.scale });

                canvas.height = viewport.height;
                canvas.width = viewport.width;

                // 渲染页面
                await page.render({
                    canvasContext: context,
                    viewport: viewport
                }).promise;

                console.log(`Rendered page ${pageNum}`);
            } catch (error) {
                console.error('Error rendering page:', error);
            }
        },

        // 翻页功能
        changePage(direction) {
            const newPage = this.currentPage + direction;
            if (newPage >= 1 && newPage <= this.totalPages) {
                this.currentPage = newPage;
                this.renderPage(this.currentPage);
            }
        },

        // 放大功能
        zoomIn() {
            this.scale *= 1.2;
            this.renderPage(this.currentPage);
        },

        // 缩小功能
        zoomOut() {
            this.scale /= 1.2;
            this.renderPage(this.currentPage);
        }
    }
};

</script>

<style scoped>
.pdf-controls {
    margin-top: 10px;
    text-align: center;
}

.pdf-controls button {
    margin: 0 10px;
}
</style>
