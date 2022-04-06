import React, {Component} from 'react';

import './Senders.css';
import '../common/Common.css'

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session';
import RemoveModal from '../common/RemoveModal';

import SenderDetailsModal from './SenderDetailsModal';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TablePagination from '@material-ui/core/TablePagination';
import Edit from '@material-ui/icons/Edit';
import Delete from '@material-ui/icons/Delete';
import Button from '@material-ui/core/Button';

class Senders extends Component {
  constructor(props) {
    super(props);

    this.state = {
      selectedSender: {},
      senders: [],
      count: 0,
      rowsPerPage: 10,
      page: 0,
      showAddSenderModal: false,
      showRemoveSenderModal: false
    };

    this.handleChangePage = this.handleChangePage.bind(this);
    this.handleChangeRowsPerPage = this.handleChangeRowsPerPage.bind(this);
    this.showAddSenderModal = this.showAddSenderModal.bind(this);
    this.closeEditModal = this.closeEditModal.bind(this);
    this.editSender = this.editSender.bind(this);
    this.removeSelectedSender = this.removeSelectedSender.bind(this);
    this.closeRemoveModal = this.closeRemoveModal.bind(this);
    this.removeSender = this.removeSender.bind(this);
  }

  removeSelectedSender() {
    CampaignsAPI.removeSender(this.state.selectedSender, response => {
      if (response.status === 'OK') {
        this.closeRemoveModal();
        this.updateSenders();
      }
    });
  }

  removeSender(sender) {
    this.setState({
      selectedSender: sender,
      showRemoveSenderModal: true,
      title: 'Remove sender',
      message: 'Are you sure you want to remove this sender?'})
  }

  closeRemoveModal(sender) {
    this.setState({selectedSender: {}, showRemoveSenderModal: false})
  }

  editSender(sender) {
    this.setState({selectedSender: sender, showAddSenderModal: true})
  }

  showAddSenderModal() {
    this.setState({showAddSenderModal: true});
  }

  closeEditModal() {
    this.setState({selectedSender: {}, showAddSenderModal: false, page: 0});
    this.updateSenders();
  }

  componentDidMount() {
    this.updateSenders();
  }

  handleChangePage(event, value) {
    this.setState({page: value}, this.updateSenders);
  }

  handleChangeRowsPerPage(event) {
    this.setState({rowsPerPage: event.target.value}, this.updateSenders);
  }

  prettyDate(dateMS) {
    if (dateMS > 0) {
      const date = new Date(dateMS);
      return date.toLocaleDateString(date) + ' ' + date.toLocaleTimeString(date);
    } else {
      return 'empty';
    }
  }

  updateSenders() {
    const request = {
      userId: Session.getUser().id,
      page: this.state.page,
      limit: this.state.rowsPerPage,
      search: this.state.search
    };

    CampaignsAPI.getSenders(request,
    response => {
      if (response.status === "OK") {
        this.setState({
          senders: response.data.senders,
          count: response.data.count
        });
      }
    });
  }

  render() {
    return (
      <div className='max-width-800'>
        <Button
          type="button"
          variant="contained"
          color="primary"
          onClick={(e) => this.showAddSenderModal()}
          className="right">Add New Sender</Button>

        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Phone</TableCell>
              <TableCell>Sent Count</TableCell>
              <TableCell>Date</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {this.state.senders.map(row => (
              <TableRow key={row.id}>
                <TableCell>{row.name}</TableCell>
                <TableCell>+{row.phone}</TableCell>
                <TableCell>{row.sentCount}</TableCell>
                <TableCell>{this.prettyDate(row.date)}</TableCell>
                <TableCell className='action-cell'>
                  <Edit
                    className='pointer right-margin-10'
                    color='primary'
                    onClick={(e) => this.editSender(row)}/>
                  <Delete
                    className='pointer'
                    color='primary'
                    onClick={(e) => this.removeSender(row)}/>
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

        {this.state.showAddSenderModal &&
          <SenderDetailsModal
            selectedSender={this.state.selectedSender}
            handleClose={this.closeEditModal} />
        }

        {this.state.showRemoveSenderModal &&
          <RemoveModal
            title={this.state.title}
            message={this.state.message}
            remove={this.removeSelectedSender}
            close={this.closeRemoveModal} />
        }
      </div>
    )
  }
}

export default Senders;
