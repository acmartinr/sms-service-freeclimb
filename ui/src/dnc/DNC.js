import React, {Component} from 'react';

import './DNC.css';
import '../common/Common.css'

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session';
import AddDNCPhonesModal from './AddDNCPhonesModal';

import Snackbar from '@material-ui/core/Snackbar';
import IconButton from '@material-ui/core/IconButton';
import CloseIcon from '@material-ui/icons/Close';

import axios from 'axios';
import {DropzoneArea} from 'material-ui-dropzone';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Add from '@material-ui/icons/Add';
import SaveAlt from '@material-ui/icons/SaveAlt';
import CircularProgress from '@material-ui/core/CircularProgress';
import Typography from "@material-ui/core/Typography";

class DNC extends Component {
  constructor(props) {
    super(props);

    this.state = {
      lists: [],
      files: [],
      maxFileSize: 100 * 1000 * 1000,
      filesLimit: 10,
      showInfoSnackbar: false,
      infoSnackbarMessage: '',
      acceptedFiles: [],
      selectedList: {},
      updateIntervalId: '',
      showAddPhonesModalWindow: false,
      uploadedFiles: {},
      loading: false,
      errorRequest: false
    };

    this.handleFileChanged = this.handleFileChanged.bind(this);
    this.handleFile = this.handleFile.bind(this);
    this.handleSnackbarClose = this.handleSnackbarClose.bind(this);
    this.downloadList = this.downloadList.bind(this);
    this.addPhonesToList = this.addPhonesToList.bind(this);
    this.closeAddPhonesModalWindow = this.closeAddPhonesModalWindow.bind(this);
  }

  addPhonesToList(list) {
    this.setState({showAddPhonesModalWindow: true, selectedList: list});
  }

  closeAddPhonesModalWindow() {
    this.setState({showAddPhonesModalWindow: false, selectedList: {}});
    this.updateDNCLists();
  }

  downloadList(list) {
    this.setState({loading: true});
    this.setState({errorRequest: false});

    axios({
      url: '/api/dnc/lists/' + list.id,
      method: 'GET',
      timeout: 1000000,
      responseType: 'blob'
    }).then((response) => {
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', list.name);
      document.body.appendChild(link);
      link.click();
      link.remove();
    }).catch((e) => {
      this.setState({errorRequest: true})
    }).finally(() => this.setState({loading: false}));
  }

  componentDidMount() {
    this.updateDNCLists();
    this.scheduleListsUpdate();
  }

  componentWillUnmount() {
    if (this.state.updateIntervalId) {
      clearInterval(this.state.updateIntervalId);
    }
  }

  scheduleListsUpdate() {
    var that = this;

    const intervalId = setInterval(function () {
      that.updateDNCLists();
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

    axios.post('/api/dnc/lists/' + Session.getUser().id, data, {})
      .then(res => {
        if (res.statusText === 'OK') {
          this.updateDNCLists();

          var files = this.state.files.slice();
          for (var i = 0; i < files.length; i++) {
            if (file.name === res.data.message) {
              var message = 'The file ' + file.name + ' has been uploaded successfully. The new DNC phones will be added to the existed list.';
              if (res.data.data === 'size.error') {
                message = 'The file ' + file.name + ' is too large. The uploaded file shouldn\'t be more than 3MB.';
              }

              this.setState({showInfoSnackbar: true, infoSnackbarMessage: message});

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

  updateDNCLists() {
    const request = {
      userId: Session.getUser().id
    };

    CampaignsAPI.getDNCLists(request,
      response => {
        if (response.status === "OK") {
          this.setState({
            lists: response.data
          });
        }
      });
  }

  isAddOperationAvailable(list) {
    return list.name === 'DNC' || this.isAdmin();
  }

  isAdmin() {
    return Session.getUser().role === 0;
  }

  render() {
    return (
      <div className='max-width-800 padding-top-10'>
        <div>
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
              dropzoneText='Drag and drop files with DNC phones'/>

            <Snackbar
              anchorOrigin={{vertical: 'bottom', horizontal: 'left'}}
              variant="success"
              open={this.state.showInfoSnackbar}
              autoHideDuration={4000}
              onClose={this.handleSnackbarClose}
              ContentProps={{'aria-describedby': 'message-id'}}
              message={<span id="message-id">{this.state.infoSnackbarMessage}</span>}
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
              <TableCell>Updated</TableCell>
              <TableCell className='action-cell'>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {this.state.lists.map(row => (
              <TableRow key={row.id}>
                <TableCell>{row.name}</TableCell>
                <TableCell>{row.cnt === -1 ? 'indexing' : row.cnt}</TableCell>
                <TableCell>{this.prettyDate(row.date)}</TableCell>
                <TableCell className='action-cell'>
                  {this.state.loading &&
                  <CircularProgress className="custom-progress-dnc-details"/>
                  }
                  {(this.isAdmin() && !this.state.loading) && <SaveAlt
                    className='pointer right-margin-10'
                    color={row.cnt !== -1 ? this.state.errorRequest ? 'error' : 'primary' : 'disabled'}
                    onClick={(e) => row.cnt !== -1 && this.downloadList(row)}/>}
                  {this.isAddOperationAvailable(row) && <Add
                    className='pointer right-margin-10'
                    color={row.cnt !== -1 ? 'primary' : 'disabled'}
                    onClick={(e) => row.cnt !== -1 && this.addPhonesToList(row)}/>}
                  {this.state.errorRequest &&
                  <Typography color="error" component="h5" variant="subtitle2">
                    There was an error with the request. Please try again later.
                  </Typography>
                  }
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>

        {this.state.showAddPhonesModalWindow &&
        <AddDNCPhonesModal
          selectedList={this.state.selectedList}
          close={this.closeAddPhonesModalWindow}/>
        }
      </div>
    )
  }
}

export default DNC;
