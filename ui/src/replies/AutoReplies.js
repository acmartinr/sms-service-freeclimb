import React, {Component} from 'react';
import {Redirect} from 'react-router-dom';

import './AutoReplies.css';
import '../common/Common.css'

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session';
import RemoveModal from '../common/RemoveModal';

import AutoReplyDetailsModal from './AutoReplyDetailsModal';
import AutoReplyTestSMSModal from './AutoReplyTestSMSModal';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TablePagination from '@material-ui/core/TablePagination';

import Message from '@material-ui/icons/Message';
import Edit from '@material-ui/icons/Edit';
import Delete from '@material-ui/icons/Delete';

import Button from '@material-ui/core/Button';

class AutoReplies extends Component {
  constructor(props) {
    super(props);

    this.state = {
      selectedAutoReply: {},
      autoReplies: [],
      count: 0,
      rowsPerPage: 10,
      page: 0,
      showAddAutoReplyModal: false,
      showRemoveAutoReplyModal: false,
      redirect: '',
      showTestSMSModal: false,
    };

    this.handleChangePage = this.handleChangePage.bind(this);
    this.handleChangeRowsPerPage = this.handleChangeRowsPerPage.bind(this);
    this.showAddAutoReplyModal = this.showAddAutoReplyModal.bind(this);
    this.closeEditModal = this.closeEditModal.bind(this);
    this.editAutoReply = this.editAutoReply.bind(this);
    this.removeSelectedAutoReply = this.removeSelectedAutoReply.bind(this);
    this.closeRemoveModal = this.closeRemoveModal.bind(this);
    this.removeAutoReply = this.removeAutoReply.bind(this);
    this.showTestSMSModal = this.showTestSMSModal.bind(this);
    this.closeTestSMSModal = this.closeTestSMSModal.bind(this);
  }

  showTestSMSModal(autoReply) {
    this.setState({selectedAutoReply: autoReply, showTestSMSModal: true});
  }

  closeTestSMSModal() {
    this.setState({showTestSMSModal: false, selectedAutoReply: {}});
  }

  removeSelectedAutoReply() {
    CampaignsAPI.removeAutoReply(this.state.selectedAutoReply, response => {
      if (response.status === 'OK') {
        this.closeRemoveModal();
        this.updateAutoReplies();
      }
    });
  }

  removeAutoReply(autoReply) {
    this.setState({
      selectedAutoReply: autoReply,
      showRemoveAutoReplyModal: true,
      title: 'Remove auto reply',
      message: 'Are you sure you want to remove this auto reply?'})
  }

  closeRemoveModal(autoReply) {
    this.setState({selectedAutoReply: {}, showRemoveAutoReplyModal: false})
  }

  editAutoReply(autoReply) {
    this.setState({selectedAutoReply: autoReply, showAddAutoReplyModal: true})
  }

  showAddAutoReplyModal() {
    this.setState({showAddAutoReplyModal: true});
  }

  closeEditModal() {
    this.setState({selectedAutoReply: {}, showAddAutoReplyModal: false, page: 0});
    this.updateAutoReplies();
  }

  componentDidMount() {
    this.updateAutoReplies();
    //this.checkPermissions();
  }

  checkPermissions() {
    var that = this;
    CampaignsAPI.getUserUISettings({id: Session.getUser().id}, function(response) {
      if (response.status === 'OK' && response.data.find(setting => setting.skey.includes("auto.reply.enabled")).sval !== '1') {
        that.setState({redirect: 'campaigns'});
      }
    });
  }

  handleChangePage(event, value) {
    this.setState({page: value}, this.updateAutoReplies);
  }

  handleChangeRowsPerPage(event) {
    this.setState({rowsPerPage: event.target.value}, this.updateAutoReplies);
  }

  prettyDate(dateMS) {
    if (dateMS > 0) {
      const date = new Date(dateMS);
      return date.toLocaleDateString(date) + ' ' + date.toLocaleTimeString(date);
    } else {
      return 'empty';
    }
  }

  updateAutoReplies() {
    const request = {
      userId: Session.getUser().id,
      page: this.state.page,
      limit: this.state.rowsPerPage,
      search: this.state.search
    };

    CampaignsAPI.getAutoReplies(request,
    response => {
      if (response.status === "OK") {
        this.setState({
          autoReplies: response.data.autoReplies,
          count: response.data.count
        });
      }
    });
  }

  render() {
    if (this.state.redirect) {
      return (<Redirect to='/campaigns'/>);
    }

    return (
      <div className='max-width-1000 padding-top-10'>
        <Button
          type="button"
          variant="contained"
          color="primary"
          onClick={(e) => this.showAddAutoReplyModal()}
          className="right">Add New Auto Reply</Button>

        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Keywords</TableCell>
              <TableCell>Message</TableCell>
              <TableCell>Date</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {this.state.autoReplies.map(row => (
              <TableRow key={row.id}>
                <TableCell>{row.keywords}</TableCell>
                <TableCell>{row.message}</TableCell>
                <TableCell>{this.prettyDate(row.date)}</TableCell>
                <TableCell className='action-cell'>
                  <Edit
                    className='pointer right-margin-10'
                    color='primary'
                    onClick={(e) => this.editAutoReply(row)}/>
                  <Message
                    className='pointer right-margin-10'
                    color='primary'
                    onClick={(e) => this.showTestSMSModal(row)}/>
                  <Delete
                    className='pointer'
                    color='primary'
                    onClick={(e) => this.removeAutoReply(row)}/>
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

        {this.state.showAddAutoReplyModal &&
          <AutoReplyDetailsModal
            selectedAutoReply={this.state.selectedAutoReply}
            handleClose={this.closeEditModal} />
        }

        {this.state.showRemoveAutoReplyModal &&
          <RemoveModal
            title={this.state.title}
            message={this.state.message}
            remove={this.removeSelectedAutoReply}
            close={this.closeRemoveModal} />
        }

        {this.state.showTestSMSModal &&
          <AutoReplyTestSMSModal
            selectedAutoReply={this.state.selectedAutoReply}
            handleClose={this.closeTestSMSModal} />
        }
      </div>
    )
  }
}

export default AutoReplies;
