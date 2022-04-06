import React, {Component} from 'react';

import './SendMessageToUserModal.css';
import '../common/Common.css';

import TextField from '@material-ui/core/TextField';

import Delete from '@material-ui/icons/Delete';
import RemoveModal from '../common/RemoveModal';

import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';

import CampaignsAPI from '../campaigns/CampaignsAPI';

class SendMessageToUserModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      messages: [],
      message: '',
      showRemoveMessageModal: false,
      selectedMessage: {}
    };

    this.closeRemoveModal = this.closeRemoveModal.bind(this);
    this.removeSelectedMessage = this.removeSelectedMessage.bind(this);
  }

  componentDidMount() {
    this.updateMessages();
  }

  onTextFieldChanged(event, field) {
    this.setState({
      [field]: event.target.value
    });
  }

  closeRemoveModal() {
    this.setState({selectedMessage: {}, showRemoveMessageModal: false});
  }

  removeSelectedMessage() {
    var that = this;
    CampaignsAPI.removeUserMessage(this.state.selectedMessage, function(response) {
      that.closeRemoveModal();
      that.updateMessages();
    });
  }

  updateMessages() {
    var that = this;
    CampaignsAPI.getUserMessages(this.props.user, function(response) {
      that.setState({messages: response.data})
    });
  }

  sendMessage() {
    var that = this;
    CampaignsAPI.sendUserMessage({userId: this.props.user.id, message: this.state.message}, function() {
      that.updateMessages();
      that.setState({message: ''});
    });
  }

  prettyDate(dateMS) {
    if (dateMS > 0) {
      const date = new Date(dateMS);
      return date.toLocaleDateString(date) + ' ' + date.toLocaleTimeString(date);
    }
  }

  removeTextMessage(message) {
    this.setState({showRemoveMessageModal: true, selectedMessage: message});
  }

  render() {
    return (
      <Dialog
        open
        onClose={this.props.handleClose}
        aria-labelledby="form-dialog-title"
        className="custom-user-messages-dialog">

        <DialogTitle
          className="padding-bottom-0"
          id="form-dialog-title">Messages</DialogTitle>

        <DialogContent>
          <div>
            <TextField
              className="margin-bottom-20 admin-message-input"
              margin="dense"
              id="message"
              label="Enter your message"
              type="text"
              value={this.state.message}
              autoFocus
              onChange={(e) => this.onTextFieldChanged(e, "message")}/>

            <Button className='admin-message-button'
                    disabled={this.state.message.length === 0}
                    onClick={(e) => this.sendMessage()} color="primary">
              Send
            </Button>
          </div>

          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Date</TableCell>
                <TableCell>Message</TableCell>
                <TableCell>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {this.state.messages.map(row => (
                <TableRow className={row.read ? '' : 'message-unread'} key={row.id}>
                  <TableCell>{this.prettyDate(row.date)}</TableCell>
                  <TableCell>{row.message}</TableCell>
                  <TableCell>
                    <Delete
                      className='pointer action-icon'
                      color='primary'
                      onClick={(e) => this.removeTextMessage(row)}/>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </DialogContent>

        <DialogActions>
          <Button onClick={(e) => this.props.handleClose()} color="primary">
            Close
          </Button>
        </DialogActions>

        {this.state.showRemoveMessageModal &&
          <RemoveModal
            title="Remove message"
            message="Are you sure you want to remove this message?"
            remove={this.removeSelectedMessage}
            close={this.closeRemoveModal} />
        }
      </Dialog>
    )
  }
}

export default SendMessageToUserModal;
