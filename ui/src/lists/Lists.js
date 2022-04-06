import React, {Component} from 'react';

import './Lists.css';
import '../common/Common.css'

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session';
import RemoveModal from '../common/RemoveModal';

import Snackbar from '@material-ui/core/Snackbar';
import IconButton from '@material-ui/core/IconButton';
import CloseIcon from '@material-ui/icons/Close';
import SaveAlt from '@material-ui/icons/SaveAlt';

import axios from 'axios';
import {DropzoneArea} from 'material-ui-dropzone';

import Checkbox from '@material-ui/core/Checkbox';
import FormControlLabel from '@material-ui/core/FormControlLabel';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TablePagination from '@material-ui/core/TablePagination';
import Delete from '@material-ui/icons/Delete';

class Lists extends Component {
  constructor(props) {
    super(props);

    this.state = {
      selectedList: {},
      lists: [],
      count: 0,
      rowsPerPage: 10,
      page: 0,
      showRemoveModal: false,
      files: [],
      maxFileSize: 100 * 1000 * 1000,
      filesLimit: 10,
      showInfoSnackbar: false,
      fileName: '',
      acceptedFiles: [],
      updateIntervalId: '',
      uploadedFiles: [],
      downloadListsEnabled: Session.getUser().downloadListsEnabled,
    };

    this.handleChangePage = this.handleChangePage.bind(this);
    this.handleChangeRowsPerPage = this.handleChangeRowsPerPage.bind(this);
    this.removeSelectedList = this.removeSelectedList.bind(this);
    this.closeRemoveModal = this.closeRemoveModal.bind(this);
    this.removeList = this.removeList.bind(this);
    this.handleFileChanged = this.handleFileChanged.bind(this);
    this.handleFile = this.handleFile.bind(this);
    this.handleSnackbarClose = this.handleSnackbarClose.bind(this);
    this.downloadList = this.downloadList.bind(this);
  }

  removeSelectedList() {
    CampaignsAPI.removeList(this.state.selectedList, response => {
      if (response.status === 'OK') {
        this.closeRemoveModal();
        this.updateLists();
      }
    });
  }

  removeList(list) {
    this.setState({
      selectedList: list,
      showRemoveModal: true,
      title: 'Remove list',
      message: 'Are you sure you want to remove this list?'
    })
  }


  downloadList(list) {
    axios({
      url: '/api/lists/' + list.id,
      method: 'GET',
      responseType: 'blob'
    }).then((response) => {
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', list.name);
      document.body.appendChild(link);
      link.click();
    });
  }

  closeRemoveModal(sender) {
    this.setState({selectedList: {}, showRemoveModal: false})
  }

  componentDidMount() {
    this.updateLists();
    this.updateUISettings();
    this.scheduleListsUpdate();
  }

  updateUISettings() {
    var that = this;
    CampaignsAPI.getUserUISettings({id: Session.getUser().id}, function (response) {
      if (response.status === 'OK') {
        if (response.data && response.data.length > 0) {
          that.setState({
            downloadListsEnabled: response.data.find(setting => setting.skey.includes("download.lists.enabled")).sval
          });

          Session.updateUserProperty('downloadListsEnabled', response.data.find(setting => setting.skey.includes("download.lists.enabled")).sval);
        }
      }
    });
  }

  componentWillUnmount() {
    if (this.state.updateIntervalId) {
      clearInterval(this.state.updateIntervalId);
    }
  }

  scheduleListsUpdate() {
    var that = this;

    const intervalId = setInterval(function () {
      that.updateLists();
    }, 5000);
    this.setState({updateIntervalId: intervalId});
  }

  handleFileChanged(files) {
    this.setState({
      'files': files
    });

    for (var i = 0; i < files.length; i++) {
      if (!this.state.uploadedFiles[files[i].name]) {
        this.handleFile(files[i]);
      } else {
        this.removeFile(files[i].name);
      }
    }
  }

  handleFile(file) {
    const data = new FormData()
    data.append('file', file)

    axios.post('/api/lists/' + Session.getUser().id, data, {})
      .then(res => {
        if (res.statusText === 'OK') {
          this.updateLists();

          var files = this.state.files.slice();
          for (var i = 0; i < files.length; i++) {
            if (file.name === res.data.message) {
              this.setState({showInfoSnackbar: true, fileName: res.data.message});

              files.splice(i, 1);
              this.setState({'files': files});

              var uploadedFiles = this.state.uploadedFiles;
              uploadedFiles[file.name] = true;
              this.setState({'uploadedFiles': uploadedFiles});

              this.removeFile(file.name);

              break;
            }
          }
        }
      });
  }

  removeFile(fileName) {
    const labels = document.getElementsByClassName('dropzone-area')[0].getElementsByTagName('p');
    for (var j = 0; j < labels.length; j++) {
      if (labels[j].innerText === fileName) {
        labels[j].parentElement.remove();
        break;
      }
    }
  }

  handleChangePage(event, value) {
    this.setState({page: value}, this.updateLists);
  }

  handleChangeRowsPerPage(event) {
    this.setState({rowsPerPage: event.target.value}, this.updateLists);
  }

  handleSnackbarClose() {
    this.setState({showInfoSnackbar: false});
  }

  prettyDate(dateMS) {
    if (dateMS > 0) {
      const date = new Date(dateMS);
      return date.toLocaleDateString(date) + ' ' + date.toLocaleTimeString(date);
    } else {
      return 'empty';
    }
  }

  updateLists() {
    const request = {
      userId: Session.getUser().id,
      page: this.state.page,
      limit: this.state.rowsPerPage,
      search: this.state.search
    };

    CampaignsAPI.getLists(request,
      response => {
        if (response.status === "OK") {
          this.setState({
            lists: response.data.lists,
            count: response.data.count
          });
        }
      });
  }

  calculateLeftPhonesCount(row) {
    var result = row.cnt - row.sentCount - row.ignored - row.dnc - row.errors;
    if (result < 0) {
      result = 0;
    }

    return result;
  }

  getListCount(row) {
    if (row.cnt === -1) {
      return 'indexing';
    } else if (row.cnt === -2) {
      return 'failed';
    } else {
      return this.calculateLeftPhonesCount(row)
    }
  }

  generateInOutRatio(row) {
    var inbound = row.receivedCount;
    var outbound = row.sentCount;

    if (outbound === 0) {
      return "-";
    } else {
      return (inbound / outbound * 100).toFixed(2) + "%";
    }
  }

  render() {
    return (
      <div className='max-width-1200'>
        <div>
          <div>
            <FormControlLabel disabled control={<Checkbox checked={true}/>} label="Remove duplicates"/>
          </div>
          <div>
            <DropzoneArea
              className="dropzone-container"
              onChange={this.handleFileChanged}
              maxFileSize={this.state.maxFileSize}
              showFileNames={true}
              showAlerts={false}
              filesLimit={this.state.filesLimit}
              acceptedFiles={this.state.acceptedFiles}
              dropzoneClass='dropzone-area'
              dropzoneText='Drag and drop files with phones'/>

            <Snackbar
              anchorOrigin={{vertical: 'bottom', horizontal: 'left'}}
              variant="success"
              open={this.state.showInfoSnackbar}
              autoHideDuration={4000}
              onClose={this.handleSnackbarClose}
              ContentProps={{'aria-describedby': 'message-id'}}
              message={<span id="message-id">The file {this.state.fileName} has been uploaded successfully</span>}
              action={[
                <IconButton
                  key="close"
                  aria-label="close"
                  color="inherit"
                  onClick={this.handleSnackbarClose}>
                  <CloseIcon/>
                </IconButton>
              ]}
            />
          </div>
        </div>

        <Table className='margin-top-20'>
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Count</TableCell>
              <TableCell>Sent</TableCell>
              <TableCell>In/Out Ratio</TableCell>
              <TableCell>Ignored</TableCell>
              <TableCell>DNC</TableCell>
              <TableCell>Errors</TableCell>
              <TableCell>Date</TableCell>
              <TableCell className='action-cell'>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {this.state.lists.map(row => (
              <TableRow key={row.id}>
                <TableCell>{row.name}</TableCell>
                <TableCell>{this.getListCount(row)}</TableCell>
                <TableCell>{row.sentCount}</TableCell>
                <TableCell>{this.generateInOutRatio(row)}</TableCell>
                <TableCell>{row.ignored}</TableCell>
                <TableCell>{row.dnc}</TableCell>
                <TableCell>{row.errors}</TableCell>
                <TableCell>{this.prettyDate(row.date)}</TableCell>
                <TableCell className='action-cell'>
                  {this.state.downloadListsEnabled === '1' && <SaveAlt
                    className='pointer right-margin-10'
                    color={row.cnt !== -1 ? 'primary' : 'disabled'}
                    onClick={(e) => row.cnt !== -1 && this.downloadList(row)}/>}
                  <Delete
                    className='pointer'
                    color={row.cnt !== -1 ? 'primary' : 'disabled'}
                    onClick={(e) => row.cnt !== -1 && this.removeList(row)}/>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>

        <TablePagination
          rowsPerPageOptions={[10, 25, 50]}
          component="div"
          count={this.state.count}
          rowsPerPage={this.state.rowsPerPage}
          page={this.state.page}
          backIconButtonProps={{
            'aria-label': 'Previous Page',
          }}
          nextIconButtonProps={{
            'aria-label': 'Next Page',
          }}
          onChangePage={this.handleChangePage}
          onChangeRowsPerPage={this.handleChangeRowsPerPage}
        />

        {this.state.showRemoveModal &&
        <RemoveModal
          title={this.state.title}
          message={this.state.message}
          remove={this.removeSelectedList}
          close={this.closeRemoveModal}/>
        }
      </div>
    )
  }
}

export default Lists;
