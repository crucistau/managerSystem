<template>
  <div>
    <el-card style="width: 600px">
      <el-form ref="form" :model="sizeForm" label-width="80px" size="mini">
        <el-form-item label="上传学号">
          <el-input  v-model="sizeForm.name"></el-input>
        </el-form-item>
        <el-form-item label="文件类型">
          <el-select v-model="sizeForm.type" placeholder="请选择文件类型">
            <el-option label="开题报告" value="开题报告"></el-option>
            <el-option label="指导记录" value="指导记录"></el-option>
            <el-option label="初稿" value="初稿"></el-option>
            <el-option label="终稿" value="终稿"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="文件上传" v-model="sizeForm.file">
          <el-upload
              :limit="1"
              action=""
              :on-change="handleChange"
              :auto-upload="false">
            <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
            <div slot="tip" class="el-upload__tip" style="display: inline;margin-left: 5px">文件大小不得超过10M</div>
          </el-upload>
        </el-form-item>
        <el-form-item size="large">
          <el-button  type="success" @click="submitUpload">上传到服务器</el-button>
          <el-button>重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>

</template>

<script>
import axios from "axios";

export default {
  name: "DocMan",
  data() {
    return {
      file: {},
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")):{},
      sizeForm: {
        name: '',
        type: '',
        file: null
      }
    }
  },

  created() {

  },

  methods: {
    handleChange(file)
    {
      this.sizeForm.file = file;
    },
    submitUpload() {

      //this.$refs.upload.submit();
      console.log(this.sizeForm)

      let dataPar = this.sizeForm;
      let fdParams = new FormData();
      fdParams.append('file', dataPar.file.raw);

      this.axios.post("/file/upload",fdParams,{
        headers: {'Content-Type': 'multipart/form-data'},
        params: {
          studentName: this.user.userName,
          studentId: this.user.userId,
          type: this.sizeForm.type
        }
        }).then(res=>{
        if (res.code === '200'){
          window.opener.document.location.reload()
        }
      })
    },



  },
}
</script>

<style scoped>

</style>