<template>
  <el-form :rules="rules" :model="dataForm" ref="dataForm" label-width="150px" @submit.native.prevent>
    <el-form-item label="名称：" prop="name">
      <el-input type="text" v-model.trim="dataForm.name" clearable></el-input>
    </el-form-item>
    <el-form-item label="文件：" prop="file" >
      <el-upload
          class="upload-import"
          ref="uploadImport"
          action=""
      :on-remove="handleRemove"
      :file-list="fileList"
      :limit="1"
      :on-change="handleChange"
      :auto-upload="false"
      accept="application/zip,.zip" >  <!-- 设置接收的文件类型-->
      <!-- el-upload组件,在手动上传时,禁用按钮外, 还需要设置    :disabled="hasFile"   为true禁用该组件,会导致上传列表也被禁用,无法删除,因此使用v-show,文件数量为1时,显示禁用的的按钮, slot不绑定trigger事件 -->
      <el-button v-show="!hasFile" slot="trigger" size="small" type="primary" >选取文件</el-button>
      <el-button v-show="hasFile"  size="small" type="primary" disabled>选取文件</el-button>
      <div slot="tip" class="el-upload__tip">只能上传zip文件，且不超过10M</div>
      </el-upload>
    </el-form-item>
  </el-form>

</template>

<script>
export default {
  name: "UploadFile",
  data() {
    return {
      dataForm: {
        name: '',
        file: null
      },
      rules: {
        name: [
          {required: true, message: "请输入名称", trigger: "blur"},
          {max: 50, message: "最长可输入50个字符", trigger: "blur"},
          {validator: isvalidatName, trigger: "blur"},
        ],
        file: [
          {required: true, message: "请选择上传文件", trigger: "blur"},
        ]
      },
      hasFile: false,
      fileList: []
    }},

  methods: {
      handleRemove(file, fileList)
      {
        if (!fileList.length) {
          this.hasFile = false;
        }
        this.dataForm.file = null;
      }
    ,
      handleChange(file, fileList)
      {
        if (fileList.length >= 2) {
          return;
        }
        if (fileList.length === 1) {
          this.hasFile = true;
        }
        this.dataForm.file = file;
      },
      //确定按钮
      onSureHandle(){
        this.$refs.dataForm.validate(valid => {
          if (valid) {
            let dataPar = this.dataForm;
            let fdParams = new FormData();
            //fdParams.append('name', dataPar.name);
            fdParams.append('file', dataPar.file.raw);

            axios.post('/interface/importProject', fdParams, {
              headers: {'Content-Type': 'multipart/form-data'},//定义内容格式,很重要
              timeout: 20000,
            }).then(response => {
              //...some logic
            }).catch(err => {
            })
          }
        })
      }

  }
}
</script>

<style scoped>

</style>