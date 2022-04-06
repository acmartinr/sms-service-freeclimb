import React, {Component} from 'react';

import './MessagesModal.css';
import '../common/Common.css';

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

class MessagesModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      messages: this.props.messages
    };
  }

  componentDidMount() {
    CampaignsAPI.resetUnreadMessages();
  }

  prettyDate(dateMS) {
    if (dateMS > 0) {
      const date = new Date(dateMS);
      return date.toLocaleDateString(date) + ' ' + date.toLocaleTimeString(date);
    }
  }

  render() {
    return (
      <Dialog
        open
        onClose={this.props.handleClose}
        aria-labelledby="form-dialog-title"
        className="custom-messages-dialog">

        <DialogTitle
          className="padding-bottom-0"
          id="form-dialog-title">Messages</DialogTitle>

        <DialogContent>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Date</TableCell>
                <TableCell>Message</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {this.state.messages.map(row => (
                <TableRow className={row.read ? '' : 'message-unread'} key={row.id}>
                  <TableCell>{this.prettyDate(row.date)}</TableCell>
                  <TableCell>{row.message}</TableCell>
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
      </Dialog>
    )
  }
}

export default MessagesModal;
